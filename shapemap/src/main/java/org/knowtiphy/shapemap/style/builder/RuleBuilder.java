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
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class RuleBuilder<S, F> {

	private IFeatureFunction<F, Boolean> filter = RuleBuilder::defaultRuleFilter;

	private final List<ISymbolizer<S, F>> graphicSymbolizers = new ArrayList<>();

	private final List<TextSymbolizer<S, F>> textSymbolizers = new ArrayList<>();

	private boolean elseFilter = false;

	public void filter(IFeatureFunction<F, Boolean> filter) {
		this.filter = filter;
	}

	public void graphicSymbolizer(ISymbolizer<S, F> symbolizer) {
		graphicSymbolizers.add(symbolizer);
	}

	public void textSymbolizer(TextSymbolizer<S, F> symbolizer) {
		textSymbolizers.add(symbolizer);
	}

	public void elseFilter() {
		this.elseFilter = true;
	}

	private static <F> boolean defaultRuleFilter(F feature, Geometry geom) {
		return true;
	}

	public Rule<S, F> build() {
		return new Rule<>(filter, graphicSymbolizers, textSymbolizers, elseFilter);
	}

}
