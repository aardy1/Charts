/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts;

import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.RenderableGeometry;
import org.knowtiphy.shapemap.context.RenderGeomCache;

/**
 * @author graham
 */
public class MemRenderGeomCache extends RenderGeomCache<MemFeature> {

    public MemRenderGeomCache(IFeatureAdapter<MemFeature> featureAdapter) {
        super(featureAdapter);
    }

    @Override
    public RenderableGeometry getRenderableGeometry(MemFeature feature) {
        var renderable = feature.getRenderableGeometry();
        if (renderable == null) System.out.println("Renderable miss : " + feature.geometryType());
        return renderable != null
                ? renderable
                : super.getRenderableGeometry(feature.defaultGeometry());
    }
}
