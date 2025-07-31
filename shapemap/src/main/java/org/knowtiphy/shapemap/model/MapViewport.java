package org.knowtiphy.shapemap.model;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.renderer.RendererUtilities;

/**
 * A map viewport -- the bounds of the viewport in world coordinates, the screen area of the
 * viewport in pixels, and transforms between the two.
 */
public class MapViewport {
    private ReferencedEnvelope bounds;

    private final boolean matchingAspectRatio;

    private Rectangle2D screenArea;

    private Affine screenToWorld;

    private Affine worldToScreen;

    private boolean hasCenteringTransforms;

    public MapViewport(ReferencedEnvelope bounds, boolean matchAspectRatio)
            throws TransformException, NonInvertibleTransformException {
        this.screenArea = Rectangle2D.EMPTY;
        this.hasCenteringTransforms = false;
        this.matchingAspectRatio = matchAspectRatio;
        this.bounds = bounds;
        setTransforms();
    }

    public ReferencedEnvelope bounds() {
        return bounds;
    }

    public void setBounds(ReferencedEnvelope bounds)
            throws TransformException, NonInvertibleTransformException {
        this.bounds = bounds;
        setTransforms();
    }

    public Rectangle2D screenArea() {
        return screenArea;
    }

    public void setScreenArea(Rectangle2D screenArea)
            throws TransformException, NonInvertibleTransformException {
        this.screenArea = screenArea;
        setTransforms();
    }

    public Affine screenToWorld() {
        return screenToWorld;
    }

    public Affine worldToScreen() {
        return worldToScreen;
    }

    private void setTransforms() throws TransformException, NonInvertibleTransformException {
        if (screenArea.equals(Rectangle2D.EMPTY)) {
            screenToWorld = worldToScreen = null;
            hasCenteringTransforms = false;
        } else if (bounds.isEmpty()) {
            screenToWorld = new Affine();
            worldToScreen = new Affine();
            hasCenteringTransforms = false;
        } else if (matchingAspectRatio) {
            if (!hasCenteringTransforms) {
                calculateCenteringTransforms();
            }
            bounds = calculateActualBounds();
        } else {
            calculateSimpleTransforms(bounds);
            hasCenteringTransforms = false;
        }
    }

    /**
     * Calculates transforms suitable for aspect ratio matching. The world bounds will be centred in
     * the screen area.
     */
    private void calculateCenteringTransforms() throws NonInvertibleTransformException {
        double xscale = screenArea.getWidth() / bounds.getWidth();
        double yscale = screenArea.getHeight() / bounds.getHeight();

        double scale = Math.min(xscale, yscale);

        double xoff =
                bounds.getMedian(0) * scale - (screenArea.getMinX() + screenArea.getWidth() / 2);
        double yoff =
                bounds.getMedian(1) * scale + (screenArea.getMinY() + screenArea.getHeight() / 2);

        worldToScreen = new Affine(scale, 0, 0, -scale, -xoff, yoff);
        screenToWorld = worldToScreen.createInverse();
        hasCenteringTransforms = true;
    }

    /**
     * Calculates transforms suitable for no aspect ratio matching.
     *
     * @param requestedBounds requested display area in world coordinates
     */
    private void calculateSimpleTransforms(ReferencedEnvelope requestedBounds)
            throws TransformException, NonInvertibleTransformException {
        worldToScreen =
                RendererUtilities.worldToScreenTransform(
                        requestedBounds,
                        screenArea,
                        requestedBounds.getCoordinateReferenceSystem());
        screenToWorld = worldToScreen.createInverse();
    }

    /** Calculates the world bounds of the current screen area. */
    private ReferencedEnvelope calculateActualBounds() {
        Point2D p0 = new Point2D(screenArea.getMinX(), screenArea.getMinY());
        Point2D p1 = new Point2D(screenArea.getMaxX(), screenArea.getMaxY());
        p0 = screenToWorld.transform(p0);
        p1 = screenToWorld.transform(p1);

        return new ReferencedEnvelope(
                Math.min(p0.getX(), p1.getX()),
                Math.max(p0.getX(), p1.getX()),
                Math.min(p0.getY(), p1.getY()),
                Math.max(p0.getY(), p1.getY()),
                bounds.getCoordinateReferenceSystem());
    }
}

/// **
// * Sets whether to adjust input world bounds to match the aspect ratio of the screen
// * area.
// *
// * @param enabled whether to enable aspect ratio adjustment
// */
// public void setMatchingAspectRatio(boolean enabled)
//  throws TransformException, NonInvertibleTransformException
// {
//  matchingAspectRatio = enabled;
//  // setTransforms(true);
// }
//
//  public void setCoordinateReferenceSystem(CoordinateReferenceSystem crs)
//    throws TransformException, NonInvertibleTransformException, FactoryException
//  {
//
//    if(crs == null)
//    {
//      bounds = new ReferencedEnvelope(bounds, null);
//    }
//    else if(!CRS.equalsIgnoreMetadata(crs, bounds.getCoordinateReferenceSystem()))
//    {
//      if(bounds.isEmpty())
//      {
//        bounds = new ReferencedEnvelope(crs);
//      }
//      else
//      {
//        bounds = bounds.transform(crs, true);
//        setTransforms(true);
//      }
//    }
//  }
//