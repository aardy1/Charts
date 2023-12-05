/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer;

import java.util.List;
import shapemap.renderer.api.IFeature;
import shapemap.renderer.symbolizer.basic.Rule;

/**
 * @author graham
 */
public record FeatureTypeStyle<F extends IFeature> (String featureType, boolean hasTextSymbolizers,
		List<Rule<F>> rules) {

	// public boolean applies(F feature) {
	// return feature.getTypeName().equals(featureType);
	// }
}
