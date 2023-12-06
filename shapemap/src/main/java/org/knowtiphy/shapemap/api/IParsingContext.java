/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

/**
 * @author graham
 */
public interface IParsingContext<F> {

	IFeatureFunction<F, Object> compilePropertyAccess(String name);

}
