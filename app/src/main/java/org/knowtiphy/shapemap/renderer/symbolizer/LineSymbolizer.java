/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.Stroke;
import org.knowtiphy.shapemap.renderer.feature.IFeature;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class LineSymbolizer<F extends IFeature> implements ISymbolizer<F> {

	private final StrokeInfo strokeInfo;

	public LineSymbolizer(StrokeInfo strokeInfo) {
		this.strokeInfo = strokeInfo;
	}

	@Override
	public void render(GraphicsRenderingContext context, F feature) {
		Stroke.setup(context, strokeInfo);
		Stroke.stroke(context, (Geometry) feature.getDefaultGeometry(), ((MemFeature) feature).geomType());
	}

}
