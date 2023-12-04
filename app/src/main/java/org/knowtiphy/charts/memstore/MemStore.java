/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.knowtiphy.charts.enc.ENCChart;
import org.locationtech.jts.index.strtree.STRtree;

/**
 * @author graham
 */
public class MemStore {

	// extends ContentDataStore {

	private final ENCChart map;

	private final Map<String, STRtree> featureSets = new HashMap<>();

	private final Map<String, SimpleFeatureType> featureSetTypes = new HashMap<>();

	public MemStore(ENCChart map) {
		this.map = map;
	}

	public void addSource(SimpleFeatureType type, STRtree index) {
		featureSets.put(type.getTypeName(), index);
		featureSetTypes.put(type.getTypeName(), type);
	}

	// @Override
	// protected List<Name> createTypeNames() {
	// return supportedFeatureTypes;
	// }

	// @Override
	public MemStoreFeatureSource featureSource(SimpleFeatureType type) throws IOException {
		var features = this.featureSets.get(type.getTypeName());
		var featureType = featureSetTypes.get(type.getTypeName());
		return new MemStoreFeatureSource(map, featureType, features);
	}

}
