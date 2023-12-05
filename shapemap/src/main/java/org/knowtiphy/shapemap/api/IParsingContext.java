/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.api.IFeatureFunction;

/**
 * @author graham
 */
public interface IParsingContext<F extends IFeature> {

	IFeatureFunction<F, Object> compilePropertyAccess(String name);

}
