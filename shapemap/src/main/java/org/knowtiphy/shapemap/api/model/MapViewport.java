package org.knowtiphy.shapemap.model;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.knowtiphy.shapemap.renderer.RendererUtilities;

public class MapViewport {

	private Rectangle2D screenArea;

	private Affine screenToWorld;

	private Affine worldToScreen;

	private ReferencedEnvelope bounds;

	private boolean matchingAspectRatio;

	private boolean hasCenteringTransforms;

	/**
	 * Determine if the map retains its scale (false) or its bounds (true) when the
	 * MapPane is resized.
	 */
	private boolean fixedBoundsOnResize = false;

	public MapViewport(ReferencedEnvelope bounds, boolean matchAspectRatio)
			throws TransformException, NonInvertibleTransformException, FactoryException {
		this.screenArea = Rectangle2D.EMPTY;
		this.hasCenteringTransforms = false;
		this.matchingAspectRatio = matchAspectRatio;
		this.bounds = bounds;
		setCoordinateReferenceSystem(bounds.getCoordinateReferenceSystem());
		copyBounds(bounds);
		setTransforms(true);
	}

	public MapViewport(ReferencedEnvelope bounds)
			throws TransformException, NonInvertibleTransformException, FactoryException {
		this(bounds, false);
	}

	public MapViewport() throws TransformException, NonInvertibleTransformException, FactoryException {
		this(null, false);
	}

	/**
	 * Sets whether to adjust input world bounds to match the aspect ratio of the screen
	 * area.
	 * @param enabled whether to enable aspect ratio adjustment
	 */
	public void setMatchingAspectRatio(boolean enabled) throws TransformException, NonInvertibleTransformException {
		matchingAspectRatio = enabled;
		// setTransforms(true);
	}

	public boolean isMatchingAspectRatio() {
		return matchingAspectRatio;
	}

	/**
	 * Checks if the view port bounds are empty (undefined). This will be {@code true} if
	 * either or both of the world bounds and screen bounds are empty.
	 * @return {@code true} if empty
	 */
	public boolean isEmpty() {
		return screenArea.equals(Rectangle2D.EMPTY) || bounds.isEmpty();
	}

	/**
	 * Gets the display area in world coordinates.
	 *
	 * <p>
	 * Note Well: this only covers spatial extent; you may wish to use the user data map
	 * to record the current viewport time or elevation.
	 * @return a copy of the current bounds
	 */
	public ReferencedEnvelope getBounds() {
		return bounds;
	}

	public void setBounds(ReferencedEnvelope requestedBounds)
			throws TransformException, NonInvertibleTransformException {

		copyBounds(requestedBounds);
		setTransforms(true);
	}

	private void copyBounds(ReferencedEnvelope newBounds) {
		if (newBounds == null || newBounds.isEmpty()) {
			bounds = new ReferencedEnvelope();
		}
		else {
			bounds = newBounds;
		}
	}

	public Rectangle2D getScreenArea() {
		return screenArea;
	}

	public void setScreenArea(Rectangle2D screenArea) throws TransformException, NonInvertibleTransformException {
		if (screenArea == null || screenArea.equals(Rectangle2D.EMPTY)) {
			this.screenArea = Rectangle2D.EMPTY;
		}
		else {
			this.screenArea = new Rectangle2D(screenArea.getMinY(), screenArea.getMinY(), screenArea.getWidth(),
					screenArea.getHeight());
		}
		if (fixedBoundsOnResize) {
			setTransforms(true);
		}
		else {
			setTransforms(false);
		}
	}

	/**
	 * The coordinate reference system used for rendering the map. If not yet set,
	 * {@code null} is returned.
	 *
	 * <p>
	 * The coordinate reference system used for rendering is often considered to be the
	 * "world" coordinate reference system; this is distinct from the coordinate reference
	 * system used for each layer (which is often data dependent).
	 * @return coordinate reference system used for rendering the map (may be
	 * {@code null}).
	 */
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return bounds.getCoordinateReferenceSystem();
	}

	public void setCoordinateReferenceSystem(CoordinateReferenceSystem crs)
			throws TransformException, NonInvertibleTransformException, FactoryException {

		if (crs == null) {
			bounds = new ReferencedEnvelope(bounds, null);
		}
		else if (!CRS.equalsIgnoreMetadata(crs, bounds.getCoordinateReferenceSystem())) {
			if (bounds.isEmpty()) {
				bounds = new ReferencedEnvelope(crs);
			}
			else {
				bounds = bounds.transform(crs, true);
				setTransforms(true);
			}
		}
	}

	public Affine getScreenToWorld() {
		return screenToWorld;
	}

	public Affine getWorldToScreen() {
		return worldToScreen;
	}

	private void setTransforms(boolean newBounds) throws TransformException, NonInvertibleTransformException {
		if (screenArea.equals(Rectangle2D.EMPTY)) {
			screenToWorld = worldToScreen = null;
			hasCenteringTransforms = false;
		}
		else if (bounds.isEmpty()) {
			screenToWorld = new Affine();
			worldToScreen = new Affine();
			hasCenteringTransforms = false;
		}
		else if (matchingAspectRatio) {
			if (newBounds || !hasCenteringTransforms) {
				calculateCenteringTransforms();
			}
			bounds = calculateActualBounds();
		}
		else {
			calculateSimpleTransforms(bounds);
			hasCenteringTransforms = false;
		}
	}

	/**
	 * Calculates transforms suitable for aspect ratio matching. The world bounds will be
	 * centred in the screen area.
	 */
	private void calculateCenteringTransforms() throws NonInvertibleTransformException {
		double xscale = screenArea.getWidth() / bounds.getWidth();
		double yscale = screenArea.getHeight() / bounds.getHeight();

		double scale = Math.min(xscale, yscale);

		double xoff = bounds.getMedian(0) * scale - (screenArea.getMinX() + screenArea.getWidth() / 2);
		double yoff = bounds.getMedian(1) * scale + (screenArea.getMinY() + screenArea.getHeight() / 2);

		worldToScreen = new Affine(scale, 0, 0, -scale, -xoff, yoff);
		screenToWorld = worldToScreen.createInverse();
		hasCenteringTransforms = true;
	}

	public boolean isFixedBoundsOnResize() {
		return fixedBoundsOnResize;
	}

	/**
	 * Determine if the map retains its scale (false) or its bounds (true) when the
	 * MapPane is resized.
	 * @param fixedBoundsOnResize - if true retain bounds on resize otherwise retain
	 * scale.
	 */
	public void setFixedBoundsOnResize(boolean fixedBoundsOnResize) {
		this.fixedBoundsOnResize = fixedBoundsOnResize;
	}

	/**
	 * Calculates transforms suitable for no aspect ratio matching.
	 * @param requestedBounds requested display area in world coordinates
	 */
	private void calculateSimpleTransforms(ReferencedEnvelope requestedBounds)
			throws TransformException, NonInvertibleTransformException {

		// why as it done this way?
		// double xscale = screenArea.getWidth() / requestedBounds.getWidth();
		// double yscale = screenArea.getHeight() / requestedBounds.getHeight();
		// worldToScreen = new Affine(xscale, 0, 0, -yscale, -xscale *
		// requestedBounds.getMinX(),
		// yscale * requestedBounds.getMaxY());

		worldToScreen = RendererUtilities.worldToScreenTransform(requestedBounds, screenArea,
				requestedBounds.getCoordinateReferenceSystem());
		screenToWorld = worldToScreen.createInverse();
	}

	/** Calculates the world bounds of the current screen area. */
	private ReferencedEnvelope calculateActualBounds() {
		Point2D p0 = new Point2D(screenArea.getMinX(), screenArea.getMinY());
		Point2D p1 = new Point2D(screenArea.getMaxX(), screenArea.getMaxY());
		p0 = screenToWorld.transform(p0);
		p1 = screenToWorld.transform(p1);

		return new ReferencedEnvelope(Math.min(p0.getX(), p1.getX()), Math.max(p0.getX(), p1.getX()),
				Math.min(p0.getY(), p1.getY()), Math.max(p0.getY(), p1.getY()), bounds.getCoordinateReferenceSystem());
	}

}
