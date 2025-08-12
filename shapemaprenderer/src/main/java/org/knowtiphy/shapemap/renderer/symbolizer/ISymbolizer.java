/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.knowtiphy.shapemap.renderer.RenderingContext;

/**
 * @author graham
 */
public interface ISymbolizer<F> {

    void render(RenderingContext<F> context, F feature);
}