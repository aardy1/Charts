/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.mark;

import org.geotools.api.feature.simple.SimpleFeature;
import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.PointSymbolizer;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 */
public interface IMarkSymbolizer {

	void render(GraphicsRenderingContext context, SimpleFeature feature, Point pt, PointSymbolizer pointSymbolizer);

}
