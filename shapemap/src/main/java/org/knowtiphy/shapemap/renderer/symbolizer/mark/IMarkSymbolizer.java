/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.mark;

import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.PointSymbolizer;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 */
public interface IMarkSymbolizer<S, F extends IFeature> {

	void render(GraphicsRenderingContext<S, F> context, F feature, Point pt, PointSymbolizer<S, F> pointSymbolizer);

}
