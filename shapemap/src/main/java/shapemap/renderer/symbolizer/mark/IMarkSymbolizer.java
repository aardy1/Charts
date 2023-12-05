/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.symbolizer.mark;

import shapemap.renderer.GraphicsRenderingContext;
import shapemap.renderer.symbolizer.PointSymbolizer;
import shapemap.renderer.api.IFeature;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 */
public interface IMarkSymbolizer<F extends IFeature> {

	void render(GraphicsRenderingContext context, F feature, Point pt, PointSymbolizer<F> pointSymbolizer);

}
