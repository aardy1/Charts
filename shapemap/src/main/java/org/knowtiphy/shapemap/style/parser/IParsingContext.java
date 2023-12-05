/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser;

import org.knowtiphy.shapemap.renderer.api.IFeature;
import org.knowtiphy.shapemap.renderer.api.IFeatureFunction;

/**
 * @author graham
 */
public interface IParsingContext<F extends IFeature> {

	IFeatureFunction<F, Object> compilePropertyAccess(String name);

}
