/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import java.util.ArrayList;
import java.util.List;
import org.knowtiphy.shapemap.renderer.FeatureTypeStyle;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Rule;

/**
 * @author graham
 */
public class FeatureTypeStyleBuilder {

	private String featureType;

	private final List<Rule> rules = new ArrayList<>();

	private boolean hasTextSymbolizers = false;

	public FeatureTypeStyleBuilder rule(Rule rule) {
		rules.add(rule);
		hasTextSymbolizers |= !rule.textSymbolizers().isEmpty();
		return this;
	}

	public FeatureTypeStyleBuilder featureType(String featureType) {
		this.featureType = featureType;
		return this;
	}

	public FeatureTypeStyle build() {
		return new FeatureTypeStyle(featureType, hasTextSymbolizers, rules);
	}

}
