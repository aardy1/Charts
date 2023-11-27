/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.mark;

import org.geotools.api.feature.simple.SimpleFeature;
import org.knowtiphy.shapemap.renderer.RenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.Fill;
import org.knowtiphy.shapemap.renderer.graphics.Stroke;
import org.knowtiphy.shapemap.renderer.symbolizer.PointSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 */
public class TriangleMarkSymbolizer extends BaseMarkSymbolizer {

	public TriangleMarkSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
		super(fillInfo, strokeInfo);
	}

	// TODO -- this is hacky
	private static final double[] x = new double[4];

	private static final double[] y = new double[4];

	@Override
	public void render(RenderingContext context, SimpleFeature feature, Point pt, PointSymbolizer pointSymbolizer) {

		if (fillInfo == null && strokeInfo == null)
			return;

		var szo = pointSymbolizer.size().apply(feature, pt);
		if (szo == null)
			return;
		var sz = ((Number) szo).doubleValue();

		var gc = context.graphicsContext();
		var tx = context.worldToScreen();

		tx.apply(pt.getX(), pt.getY());
		var sizeX = sz * context.onePixelX();
		var sizeY = sz * context.onePixelY();
		var halfSizeX = sizeX / 2;
		var halfSizeY = sizeY / 2;

		x[0] = tx.getX();
		y[0] = tx.getY() + halfSizeY;
		x[1] = tx.getX() - halfSizeX;
		y[1] = tx.getY() - halfSizeY;
		x[2] = tx.getX() + halfSizeX;
		y[2] = tx.getY() - halfSizeY;

		if (fillInfo != null) {
			Fill.setup(context, fillInfo);
			gc.fillPolygon(x, y, 3);
		}

		if (strokeInfo != null) {
			Stroke.setup(context, strokeInfo);
			x[3] = tx.getX();
			y[3] = tx.getY() + halfSizeY;
			gc.strokePolyline(x, y, 4);
		}
	}

}