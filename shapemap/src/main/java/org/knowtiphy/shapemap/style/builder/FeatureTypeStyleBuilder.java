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
public class FeatureTypeStyleBuilder<S, F> {

    private String featureType;

    private final List<Rule<S, F>> rules = new ArrayList<>();

    private boolean hasTextSymbolizers = false;

    public FeatureTypeStyleBuilder<S, F> rule(Rule<S, F> rule) {
        rules.add(rule);
        hasTextSymbolizers |= !rule.textSymbolizers().isEmpty();
        return this;
    }

    public FeatureTypeStyleBuilder<S, F> featureType(String featureType) {
        this.featureType = featureType;
        return this;
    }

    public FeatureTypeStyle<S, F> build() {
        return new FeatureTypeStyle<>(featureType, hasTextSymbolizers, rules);
    }
}
