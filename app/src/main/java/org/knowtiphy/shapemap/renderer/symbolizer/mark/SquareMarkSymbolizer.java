/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.mark;

import org.geotools.api.feature.simple.SimpleFeature;
import org.knowtiphy.shapemap.renderer.graphics.Fill;
import org.knowtiphy.shapemap.renderer.RenderingContext;
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

		if (fillInfo == null && strokeInfo == null)
			return;

		var sz = pointSymbolizer.size().apply(feature, pt);
		if (sz == null)
			return;

		var gc = context.graphicsContext();
		var tx = context.worldToScreen();

		tx.apply(pt.getX(), pt.getY());
		var sizeX = ((Number) sz).doubleValue() * context.onePixelX();
		var sizeY = ((Number) sz).doubleValue() * context.onePixelY();
		var halfSizeX = sizeX / 2;
		var halfSizeY = sizeY / 2;

		if (fillInfo != null) {
			Fill.setup(context, fillInfo);
			gc.fillRect(tx.getX() - halfSizeX, tx.getY() - halfSizeY, sizeX, sizeY);
		}

		if (strokeInfo != null) {
			Stroke.setup(context, strokeInfo);
			gc.strokeRect(tx.getX() - halfSizeY, tx.getY() - halfSizeY, sizeX, sizeY);
		}
	}

}