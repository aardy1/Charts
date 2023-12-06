/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

/**
 * An iterator over some collection of features (of a given schema type).
 *
 * @param <F> the type of the features
 */
public interface IFeatureSourceIterator<F> extends AutoCloseable {

	boolean hasNext() throws Exception;

	F next() throws Exception;

}
