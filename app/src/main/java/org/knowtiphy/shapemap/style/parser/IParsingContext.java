/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser;

import org.knowtiphy.shapemap.renderer.symbolizer.basic.IFeatureFunction;

/**
 * @author graham
 */
public interface IParsingContext {

	IFeatureFunction<Object> compilePropertyAccess(String name);

}
