/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.util.Iterator;
import org.geotools.api.data.FeatureReader;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.store.ContentState;

/**
 * @author graham
 */
public class MemStoreFeatureReader implements FeatureReader<SimpleFeatureType, SimpleFeature> {

	private final Iterator<SimpleFeature> iterator;

	private final SimpleFeatureType type;

	public MemStoreFeatureReader(ContentState state, SimpleFeatureType type, Iterator<SimpleFeature> iterator) {

		this.iterator = iterator;
		this.type = type;
	}

	@Override
	public SimpleFeatureType getFeatureType() {
		return type;
	}

	@Override
	public SimpleFeature next() {
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
