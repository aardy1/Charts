/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts;

import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.RenderableGeometry;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class MemRenderablePolygonProvider implements IRenderablePolygonProvider<MemFeature> {

    //  TODO -- can do better than this -- don't store or cache renderable geoms for points (and
    // multi-points?) -- HUH?
    /**
     * This method should never be called as the renderable geometries are cached in the features
     * themselves.
     *
     * @param geometry
     * @return nothing, always throws an UnsupportedOperationException exception
     * @throws UnsupportedOperationException
     */
    @Override
    public RenderableGeometry getRenderableGeometry(Geometry geometry) {
        return null;
        // throw new UnsupportedOperationException();
    }

    /**
     * Compute a renderable geometry for a feature's default geometry, caching the computed geometry
     * in the feature.
     *
     * @param feature the feature
     * @return the renderable geometry
     */
    @Override
    public RenderableGeometry getRenderableGeometry(MemFeature feature) {
        return switch (feature.geometryType()) {
            case POLYGON, MULTI_POLYGON, MULTI_LINE_STRING -> feature.getRenderableGeometry();
            default -> null;
        };
    }
}

        //        return switch (feature.geometryType()) {
        //            case POLYGON, MULTI_POLYGON -> feature.getRenderableGeometry();
        //            default -> null;
        //        };