/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.mark;

import org.geotools.api.feature.simple.SimpleFeature;
import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.Stroke;
import org.knowtiphy.shapemap.renderer.symbolizer.PointSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 */
public class XMarkSymbolizer extends BaseMarkSymbolizer {

	public XMarkSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
		super(fillInfo, strokeInfo);
	}

	@Override
	public void render(GraphicsRenderingContext context, SimpleFeature feature, Point pt, PointSymbolizer pointSymbolizer) {

		var szo = pointSymbolizer.size().apply(feature, pt);
		if (szo == null)
			return;
		var sz = Math.abs(((Number) szo).doubleValue());

		var x = pt.getX();
		var y = pt.getY();
		var sizeX = sz * context.onePixelX();
		var sizeY = sz * context.onePixelY();
		var halfSizeX = sizeX / 2;
		var halfSizeY = sizeY / 2;

		// TODO -- what does filling a x mean?
		// if (fillInfo != null) {
		// Fill.setup(context, fillInfo);
		// gc.fillRect(tx.getX() - halfSizeX, tx.getY() - halfSizeY, sizeX, sizeY);
		// }

		var gc = context.graphicsContext();

		if (strokeInfo != null) {
			Stroke.setup(context, strokeInfo);
			gc.strokeLine(x - halfSizeX, y - halfSizeY, x + halfSizeX, y + halfSizeY);
			gc.strokeLine(x - halfSizeX, y + halfSizeY, x + halfSizeX, y - halfSizeY);
		}
	}

}