/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import org.geotools.api.feature.simple.*;
import org.knowtiphy.charts.enc.*;
import org.locationtech.jts.index.strtree.*;

import java.util.*;

/**
 * An in memory feature store for an ENC.
 */
public class MemStore {

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

    public MemStoreFeatureSource featureSource(SimpleFeatureType type) {
        var features = this.featureSets.get(type.getTypeName());
        var featureType = featureSetTypes.get(type.getTypeName());
        return new MemStoreFeatureSource(map, featureType, features);
    }
}