/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.context;

import java.util.HashMap;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.RenderableGeometry;
import org.locationtech.jts.geom.Geometry;

/** A simple cache of rendering geometries.. */
public abstract class RenderGeomCache<F> implements IRenderablePolygonProvider<F> {

    private final HashMap<Geometry, RenderableGeometry> cache = new HashMap<>();

    private final IFeatureAdapter<F> featureAdapter;

    public RenderGeomCache(IFeatureAdapter<F> featureAdapter) {
        this.featureAdapter = featureAdapter;
    }

    @Override
    public RenderableGeometry getRenderableGeometry(Geometry geometry) {
        return cache.computeIfAbsent(geometry, this::remove);
    }

    private RenderableGeometry remove(Geometry geometry) {
        return RemoveHolesFromPolygon.remove(geometry);
    }
}