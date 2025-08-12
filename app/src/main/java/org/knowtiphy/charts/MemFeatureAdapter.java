/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts;

import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.api.FeatureGeomType;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.locationtech.jts.geom.Geometry;

/** The feature adapter for mem features */
public class MemFeatureAdapter implements IFeatureAdapter<MemFeature> {

    public static final MemFeatureAdapter ADAPTER = new MemFeatureAdapter();

    private MemFeatureAdapter() {}

    @Override
    public Geometry defaultGeometry(MemFeature feature) {
        return feature.defaultGeometry();
    }

    @Override
    public FeatureGeomType geomType(MemFeature feature) {
        return feature.geometryType();
    }

    @Override
    public FeatureGeomType geomType(Geometry geom) {
        return switch (geom.getGeometryType()) {
            case Geometry.TYPENAME_POINT -> FeatureGeomType.POINT;
            case Geometry.TYPENAME_LINESTRING -> FeatureGeomType.LINE_STRING;
            case Geometry.TYPENAME_LINEARRING -> FeatureGeomType.LINEAR_RING;
            case Geometry.TYPENAME_MULTILINESTRING -> FeatureGeomType.MULTI_LINE_STRING;
            case Geometry.TYPENAME_POLYGON -> FeatureGeomType.POLYGON;
            case Geometry.TYPENAME_MULTIPOLYGON -> FeatureGeomType.MULTI_POLYGON;
            default -> throw new IllegalArgumentException(geom.getGeometryType());
        };
    }
}