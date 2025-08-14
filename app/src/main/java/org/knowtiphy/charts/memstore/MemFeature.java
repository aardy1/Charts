/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.knowtiphy.shapemap.api.FeatureGeomType;
import org.knowtiphy.shapemap.api.RenderableGeometry;
import org.knowtiphy.shapemap.context.ComputeRenderableGeometry;
import org.locationtech.jts.geom.Geometry;

/** A feature in an in memory feature store. */
public final class MemFeature extends SimpleFeatureImpl {

    //  the default geometry of the feature
    private final Geometry defaultGeometry;

    //  the feature's geometry type as an enum (rather than a string)
    private final FeatureGeomType geometryType;

    // the features scamin
    private final Integer scaMin;

    //  the feature's geometry for stroking and filling as a renderable polygon, line, etc

    private RenderableGeometry renderableGeometry;

    public MemFeature(
            SimpleFeature feature,
            Geometry defaultGeometry,
            Integer scaMin,
            FeatureGeomType geometryType) {

        super(feature.getAttributes(), feature.getFeatureType(), feature.getIdentifier());

        this.defaultGeometry = defaultGeometry;
        this.geometryType = geometryType;
        this.scaMin = scaMin;
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

    /**
     * The scamin of the feature.
     *
     * @return the scamin
     */
    public Integer scaMin() {
        return scaMin;
    }

    public RenderableGeometry getRenderableGeometry() {
        if (renderableGeometry == null) {
            renderableGeometry = ComputeRenderableGeometry.compute(defaultGeometry);
        }
        return renderableGeometry;
    }
}