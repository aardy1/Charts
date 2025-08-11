/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.util.ArrayList;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.knowtiphy.shapemap.api.IFeatureSource;
import org.knowtiphy.shapemap.api.IFeatureSourceIterator;
import org.locationtech.jts.index.strtree.STRtree;

/**
 * @author graham
 */
public record MemStoreFeatureSource(
        SimpleFeatureType featureType, STRtree featureIndex, boolean scaleLess)
        implements IFeatureSource<MemFeature, ReferencedEnvelope> {

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
    public IFeatureSourceIterator<MemFeature> features(ReferencedEnvelope bounds, double dScale) {

        var featuresInScale = new ArrayList<MemFeature>();
        featureIndex.query(
                bounds,
                (Object obj) -> {
                    MemFeature feature = (MemFeature) obj;
                    var featureScaMin = feature.scaMin();
                    if (scaleLess || featureScaMin == null || (int) featureScaMin >= dScale) {
                        featuresInScale.add(feature);
                    }
                });

        //        List<MemFeature> featuresInBounds = featureIndex.query(bounds);
        //        var featuresInScale = new ArrayList<MemFeature>(2 * featuresInBounds.size());
        //        for (var feature : featuresInBounds) {
        //            var featureScaMin = feature.scaMin();
        //            if (scaleLess || featureScaMin == null || (int) featureScaMin >= dScale) {
        //                featuresInScale.add(feature);
        //            }
        //        }
        //  assert featuresInScale.size() == featuresInScale2.size();

        return new MemStoreFeatureIterator(featuresInScale.iterator());
    }

    public int size() {
        return featureIndex.size();
    }
}