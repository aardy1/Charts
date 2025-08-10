package org.knowtiphy.charts.chartview;

import org.knowtiphy.charts.geotools.RendererUtilities;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.geotools.Coordinates;
import org.knowtiphy.charts.platform.IUnderlyingPlatform;

/**
 * A map viewport -- the bounds of the viewport in world coordinates, the screen area of the
 * viewport in pixels, and transforms between the two.
 *
 * <p>NOTE: transforms are computed lazily, so when they are fetched, not when they are set. This
 * allows for changes to screen are and view port bounds to be batched.
 */
public class MapViewport {

    private ReferencedEnvelope bounds;
    private Rectangle2D screenArea;
    private final boolean matchAspectRatio;
    private final IUnderlyingPlatform platform;

    private Affine screenToWorld;
    private Affine worldToScreen;
    private boolean hasCenteringTransforms;

    private boolean needToRecompute = true;

    public MapViewport(
            ReferencedEnvelope bounds,
            Rectangle2D screenArea,
            IUnderlyingPlatform platform,
            boolean matchAspectRatio) {

        this.bounds = bounds;
        this.screenArea = screenArea;
        this.platform = platform;

        hasCenteringTransforms = false;
        this.matchAspectRatio = matchAspectRatio;
        //        calculateNewTransforms();
    }

    public ReferencedEnvelope getBounds() {
        return bounds;
    }

    public synchronized void setBounds(ReferencedEnvelope newBounds) {

        System.out.println("set bounds old = " + bounds);
        System.out.println("set bounds new = " + newBounds);
        var oldBounds = bounds;
        this.bounds = newBounds;
        if (!oldBounds.equals(bounds)) {
            needToRecompute = true;
        }
    }

    public Rectangle2D getScreenArea() {
        return screenArea;
    }

    public synchronized void setScreenArea(Rectangle2D newScreenArea) {

        var oldScreenArea = screenArea;
        this.screenArea = newScreenArea;
        if (!oldScreenArea.equals(screenArea)) {
            needToRecompute = true;
        }
    }

    public Affine getScreenToWorld() throws TransformException, NonInvertibleTransformException {
        calculateTransforms();
        return screenToWorld;
    }

    public Affine getWorldToScreen() throws TransformException, NonInvertibleTransformException {
        calculateTransforms();
        return worldToScreen;
    }

    public void zoomBy(double zoomFactor) {

        //  recalculate the new bounds from the original bounds and the zoom factor
        var width = bounds.getWidth();
        var height = bounds.getHeight();
        var newWidth = width * zoomFactor;
        var newHeight = height * zoomFactor;
        // expanding/shrinking mutates the envelope so copy it -- could not do the copy
        var newBounds = new ReferencedEnvelope(bounds);
        newBounds.expandBy((newWidth - width) / 2.0, (newHeight - height) / 2.0);
        setBounds(newBounds);
    }

    //  cscale 1cm : 500m means 1cm on the map is 500m in the physical world
    //  dscale 1cm : 20000m  means 1cm on the screen is 20000m in the physical world
    /**
     * The display scale -- the size of the map
     *
     * @return
     */
    public double dScale() {
        return Coordinates.dScale(getBounds(), getScreenArea().getWidth(), platform);
        //        var acrossKM = Coordinates.distanceAcross(getBounds());
        //        var screenAreaWidthPX = getScreenArea().getWidth();
        //        var screenAreaWidthCM = platform.windowWidthCM(screenAreaWidthPX);
        //        //        System.out.println("dist across M = " + acrossKM);
        //        //        System.out.println("screenAreaWidthPx = " + screenAreaWidthPX);
        //        //        System.out.println("screenAreaWidthCM = " + screenAreaWidthCM);
        //        //        System.out.println("dscale width = " + toCM(acrossKM /
        // screenAreaWidthCM));
        //
        //        //        var downKM = Coordinates.distanceDown(bounds());
        //        //        var screenAreaHeightPX = screenArea().getHeight();
        //        //        var screenAreaHeightCM = platform.windowHeightCM(screenAreaHeightPX);
        //        //        System.out.println("dist down M = " + downKM);
        //        //        System.out.println("screenAreaHeightPx = " + screenAreaHeightPX);
        //        //        System.out.println("screenAreaHeightCM = " + screenAreaHeightCM);
        //        //        System.out.println("dscale height = " + toCM(downKM /
        // screenAreaHeightCM));

        //        return toCM(acrossKM / screenAreaWidthCM);
    }

    private void calculateTransforms() throws TransformException, NonInvertibleTransformException {

        if (needToRecompute) {
            System.out.println("Recomputing xforms");
            if (screenArea.equals(Rectangle2D.EMPTY)) {
                screenToWorld = worldToScreen = null;
                hasCenteringTransforms = false;
            } else if (bounds.isEmpty()) {
                screenToWorld = new Affine();
                worldToScreen = new Affine();
                hasCenteringTransforms = false;
            } else if (matchAspectRatio) {
                if (!hasCenteringTransforms) {
                    calculateCenteringTransforms();
                }
                bounds = calculateActualBounds();
            } else {
                calculateSimpleTransforms(bounds);
                hasCenteringTransforms = false;
            }

            needToRecompute = false;
        } else System.out.println("Re-using xforms");
    }

    /**
     * Calculates transforms suitable for aspect ratio matching. The world bounds will be centered
     * in the screen area.
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