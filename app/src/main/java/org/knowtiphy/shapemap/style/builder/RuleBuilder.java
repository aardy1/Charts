/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import java.util.ArrayList;
import java.util.List;
import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.TextSymbolizer;
import org.knowtiphy.shapemap.renderer.api.IFeatureFunction;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Rule;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class RuleBuilder {

	private IFeatureFunction<?, Boolean> filter = RuleBuilder::defaultRuleFilter;

	private final List<ISymbolizer> graphicSymbolizers = new ArrayList<>();

	private final List<TextSymbolizer> textSymbolizers = new ArrayList<>();

	private boolean elseFilter = false;

	public void filter(IFeatureFunction filter) {
		this.filter = filter;
	}

	public void graphicSymbolizer(ISymbolizer symbolizer) {
		graphicSymbolizers.add(symbolizer);
	}

	public void textSymbolizer(TextSymbolizer symbolizer) {
		textSymbolizers.add(symbolizer);
	}

	public void elseFilter() {
		this.elseFilter = true;
	}

	private static <F> boolean defaultRuleFilter(F feature, Geometry geom) {
		return true;
	}

	public Rule build() {
		return new Rule(filter, graphicSymbolizers, textSymbolizers, elseFilter);
	}

}
