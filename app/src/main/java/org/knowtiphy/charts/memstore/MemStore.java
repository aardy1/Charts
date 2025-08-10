/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.util.HashMap;
import java.util.Map;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.locationtech.jts.index.strtree.STRtree;

/** An in memory feature store storing features from an ENC. */
public final class MemStore {

    private final Map<String, STRtree> featureSets = new HashMap<>();

    private final Map<String, SimpleFeatureType> featureSetTypes = new HashMap<>();

    public MemStore() {}

    public void addSource(SimpleFeatureType type, STRtree index) {
        featureSets.put(type.getTypeName(), index);
        featureSetTypes.put(type.getTypeName(), type);
    }

    public MemStoreFeatureSource featureSource(SimpleFeatureType type) {
        var features = this.featureSets.get(type.getTypeName());
        var featureType = featureSetTypes.get(type.getTypeName());
        return new MemStoreFeatureSource(featureType, features);
    }
}