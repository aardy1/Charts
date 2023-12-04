/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.Name;
import org.geotools.data.store.ContentDataStore;
import org.geotools.data.store.ContentEntry;
import org.geotools.feature.NameImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.enc.ENCChart;
import org.locationtech.jts.index.strtree.STRtree;

/**
 * @author graham
 */
public class MemStore extends ContentDataStore {

	private final ENCChart map;

	private final Map<String, STRtree> featureSets = new HashMap<>();

	private final Map<String, SimpleFeatureType> featureSetTypes = new HashMap<>();

	private final Map<String, Boolean> scaleLess = new HashMap<>();

	private final List<Name> supportedFeatureTypes = new ArrayList<>();

	public MemStore(ENCChart map) {
		this.map = map;
		// this.scale = scale;
	}

	public void addSource(SimpleFeatureType type, ReferencedEnvelope b, STRtree index, boolean scaleLss) {

		supportedFeatureTypes.add(new NameImpl(type.getTypeName()));
		featureSets.put(type.getTypeName(), index);
		featureSetTypes.put(type.getTypeName(), type);
		scaleLess.put(type.getTypeName(), scaleLss);
	}

	@Override
	protected List<Name> createTypeNames() {
		return supportedFeatureTypes;
	}

	@Override
	protected MemStoreFeatureSource createFeatureSource(ContentEntry entry) throws IOException {
		var features = this.featureSets.get(entry.getName().getLocalPart());
		var type = featureSetTypes.get(entry.getTypeName());
		var scaleLss = scaleLess.get(entry.getTypeName());
		return new MemStoreFeatureSource(entry, new MemStoreQuery(map.bounds(), scaleLss), map, type, features);
	}

	@Override
	public MemStoreFeatureSource getFeatureSource(Name typeName) throws IOException {
		return createFeatureSource(new ContentEntry(this, typeName));
	}

}
