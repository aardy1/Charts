/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.api.IFeature;
import org.knowtiphy.shapemap.renderer.graphics.Fill;
import org.knowtiphy.shapemap.renderer.graphics.Stroke;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class PolygonSymbolizer<F extends IFeature> implements ISymbolizer<F> {

	private final FillInfo fillInfo;

	private final StrokeInfo strokeInfo;

	public PolygonSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
		this.fillInfo = fillInfo;
		this.strokeInfo = strokeInfo;
	}

	@Override
	public void render(GraphicsRenderingContext context, F feature) {

		if (fillInfo != null) {
			Fill.setup(context, fillInfo);
			Fill.fill(context, (Geometry) feature.getDefaultGeometry(), feature.geomType());
		}

		if (strokeInfo != null) {
			Stroke.setup(context, strokeInfo);
			Stroke.stroke(context, (Geometry) feature.getDefaultGeometry(), feature.geomType());
		}
	}

}
