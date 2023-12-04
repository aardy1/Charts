/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.util.Iterator;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.store.ContentState;
import org.knowtiphy.shapemap.renderer.feature.IFeatureSourceIterator;

/**
 * @author graham
 */
public class MemStoreFeatureReader implements IFeatureSourceIterator<SimpleFeatureType, MemFeature> {

	private final Iterator<MemFeature> iterator;

	private final SimpleFeatureType type;

	public MemStoreFeatureReader(ContentState state, SimpleFeatureType type, Iterator<MemFeature> iterator) {

		this.iterator = iterator;
		this.type = type;
	}

	// @Override
	// public SimpleFeatureType getFeatureType() {
	// return type;
	// }

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
