/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.util.Iterator;
import org.knowtiphy.shapemap.api.IFeatureSourceIterator;

/**
 * @author graham
 */
public class MemStoreFeatureIterator implements IFeatureSourceIterator<MemFeature> {

	private final Iterator<MemFeature> iterator;

	public MemStoreFeatureIterator(Iterator<MemFeature> iterator) {
		this.iterator = iterator;
	}

	@Override
	public MemFeature next() {
		return iterator.next();
	}

	@Override
	public synchronized boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public void close() {
		// do nothing
	}

}
