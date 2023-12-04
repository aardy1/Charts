/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.api;

/**
 * @author graham
 */
public interface IFeatureSourceIterator<T, F extends IFeature> extends AutoCloseable {

	boolean hasNext();

	F next();

}
