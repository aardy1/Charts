/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.IntBinaryOperator;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.memstore.MemFeature;
import static org.knowtiphy.charts.ontology.S57.AT_SCAMAX;
import static org.knowtiphy.charts.ontology.S57.AT_SCAMIN;
import org.knowtiphy.shapemap.api.IMapLayer;
import org.locationtech.jts.geom.Geometry;

/**
 * Show various statistics for a quilt -- e.g. number of features of different types, number of
 * geometries of different types, etc.
 */
public class MapStats {

    private final Quilt<SimpleFeatureType, MemFeature> quilt;

    private final java.util.Map<String, Integer> featuresPerType = new HashMap<>();

    private final java.util.Map<String, Integer> nullMinScale = new HashMap<>();

    private final java.util.Map<String, Integer> nullMaxScale = new HashMap<>();

    private final java.util.Map<String, Integer> minScale = new HashMap<>();

    private final java.util.Map<String, Integer> maxScale = new HashMap<>();

    private final java.util.Map<String, Integer> pointGeoms = new HashMap<>();

    private final java.util.Map<String, Integer> multiPointGeoms = new HashMap<>();

    private final java.util.Map<String, Integer> lineStringGeoms = new HashMap<>();

    private final java.util.Map<String, Integer> multiLineStringGeoms = new HashMap<>();

    private final java.util.Map<String, Integer> polygonGeoms = new HashMap<>();

    private final java.util.Map<String, Integer> multiPolygonGeoms = new HashMap<>();

    private final java.util.Map<String, Integer> mixedGeoms = new HashMap<>();

    private final java.util.Map<String, Integer> totGeoms = new HashMap<>();

    public MapStats(Quilt<SimpleFeatureType, MemFeature> quilt) {
        this.quilt = quilt;
    }

    public MapStats compute() throws Exception {

        for (var map : quilt.maps()) {
            for (var layer : map.layers()) {
                scanFeatures(layer);
            }
        }

        return this;
    }

    public void print() {

        System.err.println("----------------------------------------");
        System.err.println("Quilt size = " + quilt.maps().size());
        for (var map : quilt.maps()) {
            System.err.println("\t" + map.title());
        }

        var keys = new ArrayList<>(featuresPerType.keySet());
        keys.sort(String::compareTo);

        System.err.println();
        System.err.println("Scales");
        System.err.printf(
                "%-8s %-7s %-12s %-12s %-10s %-10s%n",
                "type", "#", "#N-SCAMIN", "#N-SCAMAX", "SCAMIN", "SCAMAX");

        for (var key : keys) {
            System.err.printf(
                    "%-8s %-7d %-12s %-12s %-10s %-10s%n",
                    key,
                    featuresPerType.get(key),
                    N(nullMinScale, key),
                    N(nullMaxScale, key),
                    N(minScale, key),
                    N(maxScale, key));
        }

        System.err.println();
        System.err.println("Geometries");
        System.err.printf(
                "%-8s  %-7s %-8s %-8s %-8s %-8s %-8s %-8s %-8s %-8s%n",
                "type",
                "#",
                "#Pt",
                "#Line",
                "#Poly",
                "#M-Pt",
                "#M-Line",
                "#M-Poly",
                "#Mixed",
                "Tot Geoms");

        for (var key : keys) {
            System.err.printf(
                    "%-8s  %-7d %-8s %-8s %-8s %-8s %-8s %-8s %-8s %-8s%n",
                    key,
                    featuresPerType.get(key),
                    pointGeoms.get(key),
                    lineStringGeoms.get(key),
                    polygonGeoms.get(key),
                    multiPointGeoms.get(key),
                    multiLineStringGeoms.get(key),
                    multiPolygonGeoms.get(key),
                    mixedGeoms.get(key),
                    totGeoms.get(key));
        }

        var numFeatures = 0;
        for (var value : featuresPerType.values()) {
            numFeatures += value;
        }

        var numGeoms = 0;
        for (var value : totGeoms.values()) {
            numGeoms += value;
        }

        var numScaleLessLayers = 0;
        for (var type : totGeoms.keySet()) {
            if (!minScale.containsKey(type) && !maxScale.containsKey(type)) numScaleLessLayers++;
        }

        System.err.println();
        System.err.println("Total # features = " + numFeatures);
        System.err.println("Total # geoms = " + numGeoms);
        System.err.println("Total # scaleless layers = " + numScaleLessLayers);
        System.err.println();
    }

    //  scan all features in a layer updating counts
    private void scanFeatures(IMapLayer<SimpleFeatureType, MemFeature, ReferencedEnvelope> layer)
            throws Exception {

        try (var features = layer.featureSource().features()) {
            for (var feature : features) {
                var type = feature.getType().getTypeName();
                ensureInitialized(featuresPerType, type);
                inc(featuresPerType, type);
                updateNilCount(type, feature, AT_SCAMIN, nullMinScale);
                updateNilCount(type, feature, AT_SCAMAX, nullMaxScale);
                updateMinMaxes(type, feature, AT_SCAMIN, minScale, Integer.MAX_VALUE, Math::min);
                updateMinMaxes(type, feature, AT_SCAMAX, maxScale, Integer.MIN_VALUE, Math::max);
                updateGeomCounts(feature);
            }
        }
    }

    private void updateNilCount(
            String type,
            MemFeature feature,
            String property,
            java.util.Map<String, Integer> count) {

        var prop = feature.getProperty(property);
        ensureInitialized(count, type, 0);
        if (prop.getValue() == null) {
            inc(count, type);
        }
    }

    private void updateMinMaxes(
            String type,
            MemFeature feature,
            String property,
            java.util.Map<String, Integer> minMaxVals,
            int initialValue,
            IntBinaryOperator nextVal) {

        var prop = feature.getProperty(property);
        if (prop.getValue() != null) {
            ensureInitialized(minMaxVals, type, initialValue);
            minMaxVals.put(type, nextVal.applyAsInt(minMaxVals.get(type), (int) prop.getValue()));
        }
    }

    private void updateGeomCounts(MemFeature feature) {

        var prop = feature.getDefaultGeometryProperty().getType();
        var type = feature.getType().getTypeName();

        ensureInitialized(pointGeoms, type);
        ensureInitialized(multiPointGeoms, type);
        ensureInitialized(lineStringGeoms, type);
        ensureInitialized(multiLineStringGeoms, type);
        ensureInitialized(polygonGeoms, type);
        ensureInitialized(multiPolygonGeoms, type);
        ensureInitialized(mixedGeoms, type);
        ensureInitialized(totGeoms, type);

        switch (prop.getName().getLocalPart()) {
            case "Point" -> inc(pointGeoms, type);
            case "MultiPoint" -> inc(multiPointGeoms, type);
            case "LineString" -> inc(lineStringGeoms, type);
            case "MultiLineString" -> inc(multiLineStringGeoms, type);
            case "Polygon" -> inc(polygonGeoms, type);
            case "MultiPolygon" -> inc(multiPolygonGeoms, type);
            case "GeometryCollection" -> inc(mixedGeoms, type);
            default -> throw new IllegalArgumentException();
        }

        inc(totGeoms, type, ((Geometry) feature.getDefaultGeometry()).getNumGeometries());
    }

    private void ensureInitialized(
            java.util.Map<String, Integer> map, String property, int initialValue) {
        map.computeIfAbsent(property, k -> initialValue);
    }

    private void ensureInitialized(java.util.Map<String, Integer> map, String property) {
        ensureInitialized(map, property, 0);
    }

    private void inc(java.util.Map<String, Integer> map, String property, int increment) {
        map.put(property, map.get(property) + increment);
    }

    private void inc(java.util.Map<String, Integer> map, String property) {
        inc(map, property, 1);
    }

    private String N(java.util.Map<String, Integer> count, String type) {
        return count.containsKey(type) ? count.get(type) + "" : "N/A";
    }
}