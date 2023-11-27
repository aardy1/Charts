/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer;

import org.knowtiphy.shapemap.renderer.symbolizer.basic.Rule;
import java.util.List;
import org.geotools.api.feature.Feature;

/**
 * @author graham
 */
public record FeatureTypeStyle(String featureType, boolean hasTextSymbolizers, List<Rule> rules) {

	public boolean applies(Feature feature) {
		return feature.getName().getLocalPart().equals(featureType);
	}
}
