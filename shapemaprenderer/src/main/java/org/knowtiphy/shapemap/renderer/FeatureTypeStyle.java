/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer;

import java.util.List;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Rule;

/**
 * @author graham
 */
public record FeatureTypeStyle< F>(String featureType, boolean hasTextSymbolizers,
    List<Rule< F>> rules)
{}