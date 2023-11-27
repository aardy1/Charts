/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import org.knowtiphy.shapemap.renderer.symbolizer.basic.IFeatureFunction;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Rule;
import java.util.ArrayList;
import java.util.List;
import org.geotools.api.feature.simple.SimpleFeature;
import org.knowtiphy.shapemap.style.*;
import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.TextSymbolizer;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class RuleBuilder {

	private IFeatureFunction filter = RuleBuilder::defaultRuleFilter;

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

	private static boolean defaultRuleFilter(SimpleFeature feature, Geometry geom) {
		return true;
	}

	public Rule build() {
		return new Rule(filter, graphicSymbolizers, textSymbolizers, elseFilter);
	}

}
