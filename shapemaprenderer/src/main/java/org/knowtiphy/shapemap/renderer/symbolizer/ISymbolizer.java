/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;

/**
 * @author graham
 */
public interface ISymbolizer<F> {

    void render(GraphicsRenderingContext<F> context, F feature);
}