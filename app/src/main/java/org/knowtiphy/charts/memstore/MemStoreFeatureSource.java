/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.ontology.S57;
import org.knowtiphy.shapemap.api.IFeatureSource;
import org.knowtiphy.shapemap.api.IFeatureSourceIterator;
import org.knowtiphy.charts.chartview.view.model.MapModel;
import org.locationtech.jts.index.strtree.STRtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author graham
 */
public record MemStoreFeatureSource(
        MapModel<SimpleFeatureType, MemFeature> map,
        SimpleFeatureType featureType,
        STRtree featureIndex)
        implements IFeatureSource<SimpleFeatureType, MemFeature> {
    @Override
    @SuppressWarnings("unchecked")
    public IFeatureSourceIterator<MemFeature> features() {
        return new MemStoreFeatureIterator(
                ((List<MemFeature>) featureIndex.query(map.bounds())).iterator());
    }

    @Override
    public IFeatureSourceIterator<MemFeature> features(
            ReferencedEnvelope bounds, double displayScale, boolean scaleLess) {
        Collection<MemFeature> featuresInScale;

        var ebounds = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        List<MemFeature> featuresInBounds = featureIndex.query(bounds);
        ebounds = System.currentTimeMillis() - ebounds;

        var sbounds = System.currentTimeMillis();

        featuresInScale = new ArrayList<>();
        for (var feature : featuresInBounds) {
            var featureMinScale = feature.getAttribute(S57.AT_SCAMIN);
            if (scaleLess || featureMinScale == null || (int) featureMinScale >= displayScale) {
                featuresInScale.add(feature);
            }
        }

        sbounds = System.currentTimeMillis() - sbounds;

        //    System.err.println(
        //      "map = " + map.title() + " " + map.cScale() + " :: " + featureType.getTypeName());
        //    System.err.println("\tcurrent scale = " + displayScale + ", scaleLess = " +
        // scaleLess);
        //    System.err.println("\t\t#In Bounds = " + featuresInBounds.size() + " : time = " +
        // ebounds);
        //    System.err.println("\t\t#In Scale = " + featuresInScale.size() + " : time = " +
        // sbounds);

        return new MemStoreFeatureIterator(featuresInScale.iterator());
    }

    public int size() {
        return featureIndex.size();
    }
}