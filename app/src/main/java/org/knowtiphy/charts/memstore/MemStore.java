/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.util.HashMap;
import java.util.Map;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.locationtech.jts.index.strtree.STRtree;

/** An in memory store of features. */
public final class MemStore {

    private final Map<String, STRtree> featureSets = new HashMap<>();

    private final Map<String, SimpleFeatureType> featureSetTypes = new HashMap<>();

    public void addSource(SimpleFeatureType type, STRtree index) {
        featureSets.put(type.getTypeName(), index);
        featureSetTypes.put(type.getTypeName(), type);
    }

    public MemStoreFeatureSource featureSource(SimpleFeatureType type, boolean scaleLess) {
        var features = this.featureSets.get(type.getTypeName());
        var featureType = featureSetTypes.get(type.getTypeName());
        return new MemStoreFeatureSource(featureType, features, scaleLess);
    }
}