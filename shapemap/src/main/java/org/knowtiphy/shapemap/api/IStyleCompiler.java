/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import java.util.Collection;

/**
 * @author graham
 */
public interface IStyleCompiler<F>
{
  IFeatureFunction<F, Object> compilePropertyAccess(String propertyName);

  IFeatureFunction<F, Object> compileFunctionCall(
    String propertyName, Collection<IFeatureFunction<F, Object>> args);
}