package org.knowtiphy.shapemap.renderer;

import javafx.scene.transform.Affine;
import org.locationtech.jts.geom.Coordinate;

/**
 *
 */
public class Transformation {

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
		// src[0] = x;
		// src[1] = y;
		dest[0] = x;
		dest[1] = y;
		// transformation.transform2DPoints(src, 0, dest, 0, 1);
	}

	public void reallyApply(double x, double y) {
		src[0] = x;
		src[1] = y;
		transformation.transform2DPoints(src, 0, dest, 0, 1);
	}

	public void apply(Coordinate pd) {
		apply(pd.x, pd.y);
	}

	public void transformCoordinates(Coordinate[] coords) {

		xs = new double[coords.length];
		ys = new double[coords.length];
		for (var i = 0; i < coords.length; i++) {
			apply(coords[i].x, coords[i].y);
			xs[i] = getX();
			ys[i] = getY();
		}
	}
	//
	// public void transformCoordinates(double[] xs, double[] ys) {
	//
	// for (var i = 0; i < xs.length; i++) {
	// apply(xs[i], ys[i]);
	// xs[i] = getX();
	// ys[i] = getY();
	// }
	// }
	//
	// public void reallyTransformCoordinates(double[] xs, double[] ys) {
	//
	// for (var i = 0; i < xs.length; i++) {
	// reallyApply(xs[i], ys[i]);
	// xs[i] = getX();
	// ys[i] = getY();
	// }
	// }

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