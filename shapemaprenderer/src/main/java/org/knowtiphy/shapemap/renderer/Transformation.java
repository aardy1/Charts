package org.knowtiphy.shapemap.renderer;

import javafx.scene.transform.Affine;
import org.knowtiphy.shapemap.api.Renderable;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

/** */
public class Transformation {

    public static final Affine IDENTITY = new Affine();

    private final Affine transformation;

    private final double[] src = new double[2];

    private final double[] dest = new double[2];

    private double[] xs;

    private double[] ys;

    public Transformation(Affine transformation) {
        assert transformation != null;
        this.transformation = transformation;
    }

    /**
     * Apply the transformation to coordinate (x,y) placing the result into coordinate (dest[0],
     * dest[1])
     *
     * @param x x
     * @param y y
     */
    public void apply(double x, double y) {
        src[0] = x;
        src[1] = y;
        transformation.transform2DPoints(src, 0, dest, 0, 1);
    }

    public void apply(double[] pts) {
        var size = pts.length / 2;
        xs = new double[size];
        ys = new double[size];
        for (int i = 0; i < pts.length; i += 2) {
            apply(pts[i], pts[i + 1]);
            pts[i] = dest[0];
            pts[i + 1] = dest[1];
        }
    }

    public double[] apply(Renderable polygon) {
        var pts = new double[polygon.xs().length * 2];
        for (int i = 0, j = 0; i < polygon.xs().length; i++, j += 2) {
            pts[j] = polygon.xs()[i];
            pts[j + 1] = polygon.ys()[i];
        }

        return pts;
    }

    // TODO -- it would be nice to get rid of this -- maybe when we load the geoms?
    public void copyCoordinatesG(LineString g) {
        var numPts = g.getNumPoints();
        xs = new double[numPts];
        ys = new double[numPts];
        for (var i = 0; i < numPts; i++) {
            var coord = g.getCoordinateN(i);
            xs[i] = coord.getX();
            ys[i] = coord.getY();
        }
    }

    // TODO -- it would be nice to get rid of this -- maybe when we load the geoms?
    // polys don't have a getCoordinate() method?!?!
    public void copyCoordinatesG(Polygon g) {

        var numPts = g.getNumPoints();
        xs = new double[numPts];
        ys = new double[numPts];
        // TODO -- this is potentially a copy. JTS docs have a comment on how to avoid
        // this
        var coords = g.getCoordinates();
        for (var i = 0; i < numPts; i++) {
            xs[i] = coords[i].getX();
            ys[i] = coords[i].getY();
        }
    }

    public double getX() {
        return dest[0];
    }

    public double getY() {
        return dest[1];
    }

    public double[] getXs() {
        return xs;
    }

    public double[] getYs() {
        return ys;
    }
}