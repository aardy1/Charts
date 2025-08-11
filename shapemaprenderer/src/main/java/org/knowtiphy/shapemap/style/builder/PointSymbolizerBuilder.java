/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import org.knowtiphy.shapemap.api.IFeatureFunction;
import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.PointSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.IMarkSymbolizer;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import static org.knowtiphy.shapemap.style.parser.StyleSyntaxException.expect;

/**
 * @author graham
 */
public class PointSymbolizerBuilder<F> {

    private IMarkSymbolizer<F> markSymbolizer;

    // TODO -- spec says the default is the "native symbol size" ...?
    private IFeatureFunction<F, Number> size = (f, g) -> 8.0;

    private IFeatureFunction<F, Number> rotation;

    private double opacity = 1;

    public PointSymbolizerBuilder<F> markSymbolizer(IMarkSymbolizer<F> markSymbolizer) {
        this.markSymbolizer = markSymbolizer;
        return this;
    }

    public PointSymbolizerBuilder<F> size(IFeatureFunction<F, Number> size) {
        this.size = size;
        return this;
    }

    public PointSymbolizerBuilder<F> opacity(double opacity) {
        this.opacity = opacity;
        return this;
    }

    public PointSymbolizerBuilder<F> rotation(IFeatureFunction<F, Number> rotation) {
        this.rotation = rotation;
        return this;
    }

    public ISymbolizer<F> build() throws StyleSyntaxException {

        expect(markSymbolizer, "Expected a mark symbolizer");
        return new PointSymbolizer<>(markSymbolizer, size, opacity, rotation);
    }
}
