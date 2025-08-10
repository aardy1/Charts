/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.knowtiphy.shapemap.api.FeatureGeomType;
import org.locationtech.jts.geom.Geometry;

/** A feature in an in memory feature store. */
public final class MemFeature extends SimpleFeatureImpl {

    //  the default geometry of the feature
    private final Geometry defaultGeometry;

    //  the feature's geometry type as an enum (rather than a string)
    private final FeatureGeomType geometryType;

    public MemFeature(
            SimpleFeature feature, Geometry defaultGeometry, FeatureGeomType geometryType) {
        super(feature.getAttributes(), feature.getFeatureType(), feature.getIdentifier());
        this.defaultGeometry = defaultGeometry;
        this.geometryType = geometryType;
    }

    /**
     * The default geometry of the feature.
     *
     * @return the default geometry
     */
    public Geometry defaultGeometry() {
        return defaultGeometry;
    }

    /**
     * The geometry type of the feature.
     *
     * @return the geometry type
     */
    public FeatureGeomType geometryType() {
        return geometryType;
    }
}