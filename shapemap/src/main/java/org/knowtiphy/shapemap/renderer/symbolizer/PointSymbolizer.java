/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.DrawPoint;
import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.api.IFeatureFunction;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.IMarkSymbolizer;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class PointSymbolizer<F extends IFeature> implements ISymbolizer<F> {

	private final IMarkSymbolizer<F> markSymbolizer;

	private final IFeatureFunction<F, Number> size;

	private final IFeatureFunction<F, Number> rotation;

	private final double opacity;

	public PointSymbolizer(IMarkSymbolizer markSymbolizer, IFeatureFunction<F, Number> size, double opacity,
			IFeatureFunction<F, Number> rotation) {
		this.markSymbolizer = markSymbolizer;
		this.size = size;
		this.opacity = opacity;
		this.rotation = rotation;
	}

	public IFeatureFunction<F, Number> size() {
		return size;
	}

	public IFeatureFunction<F, Number> rotation() {
		return rotation;
	}

	@Override
	public void render(GraphicsRenderingContext context, F feature) {

		DrawPoint.setup(context, opacity);
		var geom = (Geometry) feature.getDefaultGeometry();
		for (var i = 0; i < geom.getNumGeometries(); i++) {
			markSymbolizer.render(context, feature, DrawPoint.choosePoint(geom.getGeometryN(i)), this);
		}
	}

}
