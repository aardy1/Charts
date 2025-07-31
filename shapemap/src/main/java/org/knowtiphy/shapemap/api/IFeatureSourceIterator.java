/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import java.util.Iterator;

/**
 * An iterator over a "collection" of features of type F.
 *
 * @param <F> the type of the features provided by the iterator.
 */

public interface IFeatureSourceIterator<F> extends Iterator<F>, Iterable<F>, AutoCloseable{}