package org.knowtiphy.shapemap.renderer;

import javafx.scene.transform.Affine;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

/**
 *
 */
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

	public Affine getTransformation() {
		return transformation;
	}

	public void apply(double x, double y) {
		src[0] = x;
		src[1] = y;
		transformation.transform2DPoints(src, 0, dest, 0, 1);
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