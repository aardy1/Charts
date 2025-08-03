/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer;

import org.knowtiphy.shapemap.renderer.symbolizer.basic.Rule;

import java.util.List;

/**
 * @author graham
 */
public record FeatureTypeStyle<S, F>(String featureType, boolean hasTextSymbolizers,
    List<Rule<S, F>> rules)
{}