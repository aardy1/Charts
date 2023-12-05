/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.parser;

import shapemap.renderer.api.IFeature;
import shapemap.renderer.api.IFeatureFunction;

/**
 * @author graham
 */
public interface IParsingContext<F extends IFeature> {

	IFeatureFunction<F, Object> compilePropertyAccess(String name);

}
