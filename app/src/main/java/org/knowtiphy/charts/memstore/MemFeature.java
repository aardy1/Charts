/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.knowtiphy.shapemap.api.FeatureGeomType;
import org.locationtech.jts.geom.Geometry;

import static org.locationtech.jts.geom.Geometry.TYPENAME_GEOMETRYCOLLECTION;
import static org.locationtech.jts.geom.Geometry.TYPENAME_LINEARRING;
import static org.locationtech.jts.geom.Geometry.TYPENAME_LINESTRING;
import static org.locationtech.jts.geom.Geometry.TYPENAME_MULTILINESTRING;
import static org.locationtech.jts.geom.Geometry.TYPENAME_MULTIPOINT;
import static org.locationtech.jts.geom.Geometry.TYPENAME_MULTIPOLYGON;
import static org.locationtech.jts.geom.Geometry.TYPENAME_POINT;
import static org.locationtech.jts.geom.Geometry.TYPENAME_POLYGON;

/** A feature in an in memory feature store. */
public final class MemFeature extends SimpleFeatureImpl {
    private final FeatureGeomType featureGeomType;

    private final Geometry defaultGeometry;

    public MemFeature(SimpleFeature feature) {
        super(feature.getAttributes(), feature.getFeatureType(), feature.getIdentifier());
        this.defaultGeometry = (Geometry) feature.getDefaultGeometry();
        this.featureGeomType = geomType(defaultGeometry);
    }

    public FeatureGeomType geomType() {
        return featureGeomType;
    }

    public Geometry defaultGeometry() {
        return defaultGeometry;
    }

    private static FeatureGeomType geomType(Geometry geom) {
        return switch (geom.getGeometryType()) {
            case TYPENAME_POINT -> FeatureGeomType.POINT;
            case TYPENAME_MULTIPOINT -> FeatureGeomType.MULTI_POINT;
            case TYPENAME_LINESTRING -> FeatureGeomType.LINE_STRING;
            case TYPENAME_LINEARRING -> FeatureGeomType.LINEAR_RING;
            case TYPENAME_MULTILINESTRING -> FeatureGeomType.MULTI_LINE_STRING;
            case TYPENAME_POLYGON -> FeatureGeomType.POLYGON;
            case TYPENAME_MULTIPOLYGON -> FeatureGeomType.MULTI_POLYGON;
            case TYPENAME_GEOMETRYCOLLECTION -> FeatureGeomType.GEOMETRY_COLLECTION;
            default -> throw new IllegalArgumentException(geom.getGeometryType());
        };
    }
}