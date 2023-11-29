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
public class SquareMarkSymbolizer extends BaseMarkSymbolizer {

	public SquareMarkSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
		super(fillInfo, strokeInfo);
	}

	@Override
	public void render(RenderingContext context, SimpleFeature feature, Point pt, PointSymbolizer pointSymbolizer) {

		var szo = pointSymbolizer.size().apply(feature, pt);
		if (szo == null)
			return;

		var x = pt.getX();
		var y = pt.getY();
		var sz = Math.abs(((Number) szo).doubleValue());

		var sizeX = sz * context.onePixelX();
		var sizeY = sz * context.onePixelY();
		var halfSizeX = sizeX / 2;
		var halfSizeY = sizeY / 2;

		var gc = context.graphicsContext();

		if (fillInfo != null) {
			Fill.setup(context, fillInfo);
			gc.fillRect(x - halfSizeX, y - halfSizeY, sizeX, sizeY);
		}

		if (strokeInfo != null) {
			Stroke.setup(context, strokeInfo);
			gc.strokeRect(x - halfSizeY, y - halfSizeY, sizeX, sizeY);
		}
	}

}