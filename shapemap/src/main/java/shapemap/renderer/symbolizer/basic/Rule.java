/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.symbolizer.basic;

import shapemap.renderer.api.IFeatureFunction;
import shapemap.renderer.api.IFeature;
import java.util.List;
import shapemap.renderer.symbolizer.ISymbolizer;
import shapemap.renderer.symbolizer.TextSymbolizer;

/**
 * @author graham
 */
public record Rule<F extends IFeature> (IFeatureFunction<F, Boolean> filter, List<ISymbolizer<F>> graphicSymbolizers,
		List<TextSymbolizer<F>> textSymbolizers, boolean elseFilter) {

}
