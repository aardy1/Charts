/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.mark;

import org.geotools.api.feature.simple.SimpleFeature;
import org.knowtiphy.shapemap.renderer.RenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.Stroke;
import org.knowtiphy.shapemap.renderer.symbolizer.PointSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 */
public class CrossMarkSymbolizer extends BaseMarkSymbolizer {

	public CrossMarkSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
		super(fillInfo, strokeInfo);
	}

	@Override
	public void render(RenderingContext context, SimpleFeature feature, Point pt, PointSymbolizer pointSymbolizer) {

		if (fillInfo == null && strokeInfo == null)
			return;

		var szo = pointSymbolizer.size().apply(feature, pt);
		if (szo == null)
			return;
		var sz = Math.abs(((Number) szo).doubleValue());

		var gc = context.graphicsContext();
		var tx = context.worldToScreen();

		tx.apply(pt.getX(), pt.getY());
		var sizeX = sz * context.onePixelX();
		var sizeY = sz * context.onePixelY();
		var halfSizeX = sizeX / 2;
		var halfSizeY = sizeY / 2;

		// TODO -- what does filling a cross mean?
		// if (fillInfo != null) {
		// Fill.setup(context, fillInfo);
		// gc.fillRect(tx.getX() - halfSizeX, tx.getY() - halfSizeY, sizeX, sizeY);
		// }

		if (strokeInfo != null) {
			Stroke.setup(context, strokeInfo);
			gc.strokeLine(tx.getX() - halfSizeX, tx.getY(), tx.getX() + halfSizeX, tx.getY());
			gc.strokeLine(tx.getX(), tx.getY() - halfSizeY, tx.getX(), tx.getY() + halfSizeY);
		}
	}

}