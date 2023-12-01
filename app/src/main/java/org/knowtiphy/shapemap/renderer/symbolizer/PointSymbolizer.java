/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.geotools.api.feature.simple.SimpleFeature;
import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.DrawPoint;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.IFeatureFunction;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.IMarkSymbolizer;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class PointSymbolizer implements ISymbolizer {

	private final IMarkSymbolizer markSymbolizer;

	private final IFeatureFunction<Number> size;

	private final IFeatureFunction<Number> rotation;

	private final double opacity;

	public PointSymbolizer(IMarkSymbolizer markSymbolizer, IFeatureFunction<Number> size, double opacity,
			IFeatureFunction<Number> rotation) {
		this.markSymbolizer = markSymbolizer;
		this.size = size;
		this.opacity = opacity;
		this.rotation = rotation;
	}

	public IFeatureFunction<Number> size() {
		return size;
	}

	public IFeatureFunction<Number> rotation() {
		return rotation;
	}

	@Override
	public void render(GraphicsRenderingContext context, SimpleFeature feature) {

		DrawPoint.setup(context, opacity);
		var geom = (Geometry) feature.getDefaultGeometry();
		for (var i = 0; i < geom.getNumGeometries(); i++) {
			markSymbolizer.render(context, feature, DrawPoint.choosePoint(geom.getGeometryN(i)), this);
		}
	}

}
