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
        return feature.geomType();
    }
}