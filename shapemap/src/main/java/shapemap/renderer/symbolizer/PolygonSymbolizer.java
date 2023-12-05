/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.symbolizer;

import shapemap.renderer.GraphicsRenderingContext;
import shapemap.renderer.api.IFeature;
import shapemap.renderer.graphics.Fill;
import shapemap.renderer.graphics.Stroke;
import shapemap.renderer.symbolizer.basic.FillInfo;
import shapemap.renderer.symbolizer.basic.StrokeInfo;
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
