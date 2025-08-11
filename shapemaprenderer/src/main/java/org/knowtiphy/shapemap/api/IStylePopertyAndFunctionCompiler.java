/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import java.util.Collection;

/**
 * A compiler of property accesses and function calls in style sheets into feature functions.
 *
 * @param <F> the feature type of the feature functions
 */
public interface IStylePopertyAndFunctionCompiler<F> {

    IFeatureFunction<F, Object> compilePropertyAccess(String propertyName);

    IFeatureFunction<F, Object> compileFunctionCall(
            String propertyName, Collection<IFeatureFunction<F, Object>> args);
}