/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import java.util.ArrayList;
import java.util.List;
import org.knowtiphy.shapemap.api.IFeatureFunction;
import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.TextSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Rule;

/**
 * @author graham
 */
public class RuleBuilder<F> {

    private IFeatureFunction<F, Boolean> filter = (f, g) -> true;

    private final List<ISymbolizer<F>> graphicSymbolizers = new ArrayList<>();

    private final List<TextSymbolizer<F>> textSymbolizers = new ArrayList<>();

    private boolean elseFilter = false;

    public void filter(IFeatureFunction<F, Boolean> filter) {
        this.filter = filter;
    }

    public void graphicSymbolizer(ISymbolizer<F> symbolizer) {
        graphicSymbolizers.add(symbolizer);
    }

    public void textSymbolizer(TextSymbolizer<F> symbolizer) {
        textSymbolizers.add(symbolizer);
    }

    public void elseFilter() {
        this.elseFilter = true;
    }

    public Rule<F> build() {
        return new Rule<>(filter, graphicSymbolizers, textSymbolizers, elseFilter);
    }
}