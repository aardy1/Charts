/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.util.ArrayList;
import java.util.List;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.knowtiphy.charts.ontology.S57;
import org.knowtiphy.shapemap.api.IFeatureSource;
import org.knowtiphy.shapemap.api.IFeatureSourceIterator;
import org.locationtech.jts.index.strtree.STRtree;

/**
 * @author graham
 */
public record MemStoreFeatureSource(
        SimpleFeatureType featureType, STRtree featureIndex, boolean scaleLess)
        implements IFeatureSource<SimpleFeatureType, MemFeature, ReferencedEnvelope> {

    private static final ReferencedEnvelope EVERYTHING =
            new ReferencedEnvelope(
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY,
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY,
                    DefaultGeographicCRS.WGS84);

    @Override
    @SuppressWarnings("unchecked")
    public IFeatureSourceIterator<MemFeature> features() {
        return new MemStoreFeatureIterator(featureIndex.query(EVERYTHING).iterator());
    }

    @Override
    public IFeatureSourceIterator<MemFeature> features(
            ReferencedEnvelope bounds, double dScale, boolean scaleLess) {

        List<MemFeature> featuresInBounds = featureIndex.query(bounds);

        var featuresInScale = new ArrayList<MemFeature>();
        for (var feature : featuresInBounds) {
            var featureMinScale = feature.getAttribute(S57.AT_SCAMIN);
            if (scaleLess || featureMinScale == null || (int) featureMinScale >= dScale) {
                featuresInScale.add(feature);
            }
        }

        return new MemStoreFeatureIterator(featuresInScale.iterator());
    }

    @Override
    public IFeatureSourceIterator<MemFeature> features(ReferencedEnvelope bounds, double dScale) {
        return features(bounds, dScale, scaleLess);
    }

    public int size() {
        return featureIndex.size();
    }
}