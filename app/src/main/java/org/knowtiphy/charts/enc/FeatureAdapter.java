/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.api.FeatureGeomType;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class FeatureAdapter implements IFeatureAdapter<MemFeature>
{

    public static final FeatureAdapter ADAPTER = new FeatureAdapter();

    private FeatureAdapter()
    {
    }

    @Override
    public Geometry defaultGeometry(MemFeature feature)
    {
        return feature.defaultGeometry();
    }

    @Override
    public FeatureGeomType geomType(MemFeature feature)
    {
        return feature.geomType();
    }

}