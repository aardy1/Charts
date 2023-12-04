/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer;

import java.util.List;
import org.knowtiphy.shapemap.renderer.api.IFeature;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Rule;

/**
 * @author graham
 */
public record FeatureTypeStyle<F extends IFeature> (String featureType, boolean hasTextSymbolizers,
		List<Rule<F>> rules) {

	// public boolean applies(F feature) {
	// return feature.getTypeName().equals(featureType);
	// }
}
