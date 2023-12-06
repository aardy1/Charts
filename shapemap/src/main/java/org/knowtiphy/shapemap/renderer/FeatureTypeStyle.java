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
public record FeatureTypeStyle<S, F> (String featureType, boolean hasTextSymbolizers, List<Rule<S, F>> rules) {

	// public boolean applies(F feature) {
	// return feature.getTypeName().equals(featureType);
	// }
}
