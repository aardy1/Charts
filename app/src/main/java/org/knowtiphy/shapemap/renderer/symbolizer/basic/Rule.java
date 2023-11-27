/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.basic;

import java.util.List;
import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.TextSymbolizer;

/**
 * @author graham
 */
public record Rule(IFeatureFunction filter, List<ISymbolizer> graphicSymbolizers, List<TextSymbolizer> textSymbolizers,
		boolean elseFilter) {

}
