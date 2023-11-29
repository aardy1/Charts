/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.geotools.api.feature.simple.SimpleFeature;
import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;

/**
 * @author graham
 */
public interface ISymbolizer {

	void render(GraphicsRenderingContext context, SimpleFeature feature);

}
