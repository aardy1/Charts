/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.symbolizer;

import shapemap.renderer.GraphicsRenderingContext;
import shapemap.renderer.api.IFeature;
import shapemap.renderer.graphics.Stroke;
import shapemap.renderer.symbolizer.basic.StrokeInfo;
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
		Stroke.stroke(context, (Geometry) feature.getDefaultGeometry(), feature.geomType());
	}

}
