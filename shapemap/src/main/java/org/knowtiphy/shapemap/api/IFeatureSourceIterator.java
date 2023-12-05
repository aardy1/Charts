/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

/**
 * @author graham
 */
public interface IFeatureSourceIterator<T, F extends IFeature> extends AutoCloseable {

	boolean hasNext() throws Exception;

	F next() throws Exception;

}
