/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.basic;

import java.util.List;
import org.knowtiphy.shapemap.api.IFeatureFunction;
import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.TextSymbolizer;

/** A rule in an SLD */
public record Rule<F>(
        IFeatureFunction<F, Boolean> filter,
        List<ISymbolizer<F>> graphicSymbolizers,
        List<TextSymbolizer<F>> textSymbolizers,
        boolean elseFilter) {}
