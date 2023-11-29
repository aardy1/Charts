/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.mark;

import org.geotools.api.feature.simple.SimpleFeature;
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
public class CircleMarkSymbolizer extends BaseMarkSymbolizer {

	public CircleMarkSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
		super(fillInfo, strokeInfo);
	}

	@Override
	public void render(GraphicsRenderingContext context, SimpleFeature feature, Point pt, PointSymbolizer pointSymbolizer) {

		var szo = pointSymbolizer.size().apply(feature, pt);
		if (szo == null)
			return;

		var x = pt.getX();
		var y = pt.getY();
		var sz = ((Number) szo).doubleValue();

		var sizeX = sz * context.onePixelX();
		var sizeY = sz * context.onePixelY();
		var halfSizeX = sizeX / 2;
		var halfSizeY = sizeY / 2;

		var gc = context.graphicsContext();

		if (fillInfo != null) {
			Fill.setup(context, fillInfo);
			gc.fillOval(x - halfSizeX, y - halfSizeY, sizeX, sizeY);
		}

		if (strokeInfo != null) {
			Stroke.setup(context, strokeInfo);
			gc.strokeOval(x - halfSizeX, y - halfSizeY, sizeX, sizeY);
		}
	}

}