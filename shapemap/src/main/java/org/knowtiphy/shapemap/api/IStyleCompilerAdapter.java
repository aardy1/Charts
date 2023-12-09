/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import java.util.Collection;

/**
 * @author graham
 */
public interface IStyleCompilerAdapter<F> {

	IFeatureFunction<F, Object> compilePropertyAccess(String name);

	IFeatureFunction<F, Object> compileFunctionCall(String name, Collection<IFeatureFunction<F, Object>> args);

}
