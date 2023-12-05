/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.mark;

import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.PointSymbolizer;
import org.knowtiphy.shapemap.api.IFeature;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 */
public interface IMarkSymbolizer<F extends IFeature> {

	void render(GraphicsRenderingContext context, F feature, Point pt, PointSymbolizer<F> pointSymbolizer);

}
