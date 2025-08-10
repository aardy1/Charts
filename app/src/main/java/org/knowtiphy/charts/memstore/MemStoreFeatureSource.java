/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.ontology.S57;
import org.knowtiphy.shapemap.api.IFeatureSource;
import org.knowtiphy.shapemap.api.IFeatureSourceIterator;
import org.locationtech.jts.index.strtree.STRtree;

/**
 * @author graham
 */
public record MemStoreFeatureSource(SimpleFeatureType featureType, STRtree featureIndex)
        implements IFeatureSource<SimpleFeatureType, MemFeature, ReferencedEnvelope> {

    @Override
    @SuppressWarnings("unchecked")
    public IFeatureSourceIterator<MemFeature> features() {
        return new MemStoreFeatureIterator(
                ((List<MemFeature>) featureIndex.itemsTree()).iterator());
        //                ((List<MemFeature>) featureIndex.query(map.bounds())).iterator());
    }

    @SuppressWarnings("unchecked")
    @Override
    public IFeatureSourceIterator<MemFeature> features(
            ReferencedEnvelope bounds, double displayScale, boolean scaleLess) {
        Collection<MemFeature> featuresInScale;

        List<MemFeature> featuresInBounds = featureIndex.query(bounds);
        featuresInScale = new ArrayList<>();
        for (var feature : featuresInBounds) {
            var featureMinScale = feature.getAttribute(S57.AT_SCAMIN);
            if (scaleLess || featureMinScale == null || (int) featureMinScale >= displayScale) {
                featuresInScale.add(feature);
            }
        }

        return new MemStoreFeatureIterator(featuresInScale.iterator());
    }

    public int size() {
        return featureIndex.size();
    }
}