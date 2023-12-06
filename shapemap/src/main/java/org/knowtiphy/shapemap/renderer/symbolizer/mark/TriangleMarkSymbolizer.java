/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.mark;

import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.Fill;
import org.knowtiphy.shapemap.renderer.graphics.Stroke;
import org.knowtiphy.shapemap.renderer.symbolizer.PointSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 */
public class TriangleMarkSymbolizer<S, F extends IFeature> extends BaseMarkSymbolizer<S, F> {

	public TriangleMarkSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
		super(fillInfo, strokeInfo);
	}

	// TODO -- this is hacky
	private static final double[] xs = new double[4];

	private static final double[] ys = new double[4];

	@Override
	public void render(GraphicsRenderingContext<S, F> context, F feature, Point pt,
			PointSymbolizer<S, F> pointSymbolizer) {

		var szo = pointSymbolizer.size().apply(feature, pt);
		if (szo == null)
			return;

		var x = pt.getX();
		var y = pt.getY();
		var sz = szo.doubleValue();

		var sizeX = sz * context.onePixelX();
		var sizeY = sz * context.onePixelY();
		var halfSizeX = sizeX / 2;
		var halfSizeY = sizeY / 2;

		xs[0] = x;
		ys[0] = y + halfSizeY;
		xs[1] = x - halfSizeX;
		ys[1] = y - halfSizeY;
		xs[2] = x + halfSizeX;
		ys[2] = y - halfSizeY;

		var gc = context.graphicsContext();
		if (fillInfo != null) {
			Fill.setup(context, fillInfo);
			gc.fillPolygon(xs, ys, 3);
		}

		if (strokeInfo != null) {
			Stroke.setup(context, strokeInfo);
			xs[3] = x;
			ys[3] = y + halfSizeY;
			gc.strokePolyline(xs, ys, 4);
		}
	}

}