/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.knowtiphy.shapemap.renderer.symbolizer.mark.IMarkSymbolizer;
import org.geotools.api.feature.simple.SimpleFeature;
import org.knowtiphy.shapemap.renderer.RenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.DrawPoint;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.IFeatureFunction;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class PointSymbolizer implements ISymbolizer {

	private final IMarkSymbolizer markSymbolizer;

	private final IFeatureFunction size;

	private final double opacity;

	public PointSymbolizer(IMarkSymbolizer markSymbolizer, IFeatureFunction size, double opacity) {
		this.markSymbolizer = markSymbolizer;
		this.size = size;
		this.opacity = opacity;
	}

	public IFeatureFunction size() {
		return size;
	}

	@Override
	public void render(RenderingContext context, SimpleFeature feature) {

		DrawPoint.setup(context, opacity);
		var geom = (Geometry) feature.getDefaultGeometry();
		for (var i = 0; i < geom.getNumGeometries(); i++) {
			markSymbolizer.render(context, feature, DrawPoint.choosePoint(geom.getGeometryN(i)), this);
		}
	}

}
