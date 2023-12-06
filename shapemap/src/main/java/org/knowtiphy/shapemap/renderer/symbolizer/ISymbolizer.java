/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;

/**
 * @author graham
 */
public interface ISymbolizer<S, F extends IFeature> {

	void render(GraphicsRenderingContext<S, F> context, F feature);

}
