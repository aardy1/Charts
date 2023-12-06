/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.Fill;
import org.knowtiphy.shapemap.renderer.graphics.Stroke;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;

/**
 * @author graham
 */
public class PolygonSymbolizer<S, F extends IFeature> implements ISymbolizer<S, F> {

	private final FillInfo fillInfo;

	private final StrokeInfo strokeInfo;

	public PolygonSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
		this.fillInfo = fillInfo;
		this.strokeInfo = strokeInfo;
	}

	@Override
	public void render(GraphicsRenderingContext<S, F> context, F feature) {

		if (fillInfo != null) {
			Fill.setup(context, fillInfo);
			Fill.fill(context, feature.getDefaultGeometry(), feature.geomType());
		}

		if (strokeInfo != null) {
			Stroke.setup(context, strokeInfo);
			Stroke.stroke(context, feature.getDefaultGeometry(), feature.geomType());
		}
	}

}
