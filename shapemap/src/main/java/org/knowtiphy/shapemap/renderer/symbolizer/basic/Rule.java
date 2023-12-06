/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.basic;

import java.util.List;
import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.api.IFeatureFunction;
import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.TextSymbolizer;

/**
 * @author graham
 */
public record Rule<S, F extends IFeature> (IFeatureFunction<F, Boolean> filter,
		List<ISymbolizer<S, F>> graphicSymbolizers, List<TextSymbolizer<S, F>> textSymbolizers, boolean elseFilter) {

}
