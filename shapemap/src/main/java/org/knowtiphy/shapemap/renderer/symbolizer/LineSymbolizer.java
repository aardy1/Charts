/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.Stroke;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;

/**
 * @author graham
 */
public class LineSymbolizer<S, F extends IFeature> implements ISymbolizer<S, F> {

	private final StrokeInfo strokeInfo;

	public LineSymbolizer(StrokeInfo strokeInfo) {
		this.strokeInfo = strokeInfo;
	}

	@Override
	public void render(GraphicsRenderingContext<S, F> context, F feature) {
		Stroke.setup(context, strokeInfo);
		Stroke.stroke(context, feature.getDefaultGeometry(), feature.geomType());
	}

}
