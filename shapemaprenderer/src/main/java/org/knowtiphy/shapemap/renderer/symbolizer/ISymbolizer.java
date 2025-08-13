/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.knowtiphy.shapemap.renderer.RenderingContext;

/**
 * A symbolizer for features.
 *
 * @param <F> the type of the features.
 */
public interface ISymbolizer<F> {

    void render(RenderingContext<F> context, F feature);
}