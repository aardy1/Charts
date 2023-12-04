/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.geotools.api.feature.simple.SimpleFeature;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.Fill;
import org.knowtiphy.shapemap.renderer.graphics.Stroke;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class PolygonSymbolizer implements ISymbolizer {

	private final FillInfo fillInfo;

	private final StrokeInfo strokeInfo;

	public PolygonSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
		this.fillInfo = fillInfo;
		this.strokeInfo = strokeInfo;
	}

	@Override
	public void render(GraphicsRenderingContext context, SimpleFeature feature) {

		if (fillInfo != null) {
			Fill.setup(context, fillInfo);
			Fill.fill(context, (Geometry) feature.getDefaultGeometry(), ((MemFeature) feature).geomType());
		}

		if (strokeInfo != null) {
			Stroke.setup(context, strokeInfo);
			Stroke.stroke(context, (Geometry) feature.getDefaultGeometry(), ((MemFeature) feature).geomType());
		}
	}

}
