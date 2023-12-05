/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.basic;

import org.knowtiphy.shapemap.api.IFeatureFunction;
import org.knowtiphy.shapemap.api.IFeature;
import java.util.List;
import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.TextSymbolizer;

/**
 * @author graham
 */
public record Rule<F extends IFeature> (IFeatureFunction<F, Boolean> filter, List<ISymbolizer<F>> graphicSymbolizers,
		List<TextSymbolizer<F>> textSymbolizers, boolean elseFilter) {

}
