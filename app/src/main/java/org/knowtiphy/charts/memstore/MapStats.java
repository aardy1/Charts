/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import org.geotools.api.feature.simple.SimpleFeatureType;
import org.knowtiphy.charts.ontology.ENC;
import org.knowtiphy.charts.chartview.view.model.MapLayer;
import org.knowtiphy.charts.chartview.view.model.MapModel;
import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntBinaryOperator;

import static org.knowtiphy.charts.geotools.Coordinates.distanceAcross;
import static org.knowtiphy.charts.ontology.S57.AT_SCAMAX;
import static org.knowtiphy.charts.ontology.S57.AT_SCAMIN;

/**
 * @author graham
 */
public class MapStats {
    private final List<MapModel<SimpleFeatureType, MemFeature>> maps;

    private final Map<String, Integer> counts = new HashMap<>();

    private final Map<String, Integer> nullMinScale = new HashMap<>();

    private final Map<String, Integer> nullMaxScale = new HashMap<>();

    private final Map<String, Integer> minScale = new HashMap<>();

    private final Map<String, Integer> maxScale = new HashMap<>();

    private final Map<String, Integer> pointGeoms = new HashMap<>();

    private final Map<String, Integer> multiPointGeoms = new HashMap<>();

    private final Map<String, Integer> lineStringGeoms = new HashMap<>();

    private final Map<String, Integer> multiLineStringGeoms = new HashMap<>();

    private final Map<String, Integer> polygonGeoms = new HashMap<>();

    private final Map<String, Integer> multiPolygonGeoms = new HashMap<>();

    private final Map<String, Integer> mixedGeoms = new HashMap<>();

    private final Map<String, Integer> totGeoms = new HashMap<>();

    public MapStats(List<MapModel<SimpleFeatureType, MemFeature>> maps) {
        this.maps = maps;
    }

    public MapStats stats() {
        try {
            for (var map : maps) {
                for (var layer : map.layers()) {
                    //                    var layerSize = ((MemStoreFeatureSource)
                    // layer.featureSource()).size();
                    //                    var type =
                    // adapter.name(layer.featureSource().getSchema());
                    //                    counts.put(type, layerSize);
                    featureScan(layer);
                }
            }
        } catch (Exception ex) {
            //  ignore
        }

        return this;
    }

    public void print() {
        var numFeatures = 0;
        var numGeoms = 0;

        System.err.println("----------------------------------------");
        System.err.println("Quilt size = " + maps.size());
        for (var map : maps) {
            System.err.println("\t" + map.title());
        }

        try {
            for (var map : maps) {
                for (var layer : map.layers()) {
                    var layerSize = ((MemStoreFeatureSource) layer.featureSource()).size();
                    numFeatures += layerSize;
                }

                System.err.println("NF = " + map.title() + " " + numFeatures);

                for (var value : totGeoms.values()) {
                    numGeoms += value;
                }
            }
        } catch (Exception ex) {
            //  ignore
        }

        var keys = new ArrayList<>(counts.keySet());
        keys.sort(String::compareTo);

        System.err.println();
        System.err.println("Scale Summary");
        System.err.printf(
                "%-8s %-7s %-12s %-12s %-10s %-10s%n",
                "type", "#", "#N-SCAMIN", "#N-SCAMAX", "SCAMIN", "SCAMAX");
        for (var key : keys) {
            System.err.printf(
                    "%-8s %-7d %-12s %-12s %-10s %-10s%n",
                    key,
                    counts.get(key),
                    N(nullMinScale.get(key)),
                    N(nullMaxScale.get(key)),
                    N(minScale.get(key)),
                    N(maxScale.get(key)));
        }

        System.err.println();
        System.err.println("Geometry Summary");
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
                    counts.get(key),
                    pointGeoms.get(key),
                    lineStringGeoms.get(key),
                    polygonGeoms.get(key),
                    multiPointGeoms.get(key),
                    multiLineStringGeoms.get(key),
                    multiPolygonGeoms.get(key),
                    mixedGeoms.get(key),
                    totGeoms.get(key));
        }

        System.err.println();
        System.err.println("Total num features = " + numFeatures);
        System.err.println("Total geoms = " + numGeoms);
        System.err.println();

        var mapSpans = distanceAcross(maps.get(0).bounds()) / 1000;
        System.err.println("Map span = " + mapSpans + " km");
        System.err.println("Map span = " + ENC.kmToNM(mapSpans) + " nm");
        System.err.println("----------------------------------------");
        System.err.println();
    }

    public Map<String, Integer> getMinScale() {
        return minScale;
    }

    public Map<String, Integer> getMaxScale() {
        return maxScale;
    }

    private String N(Integer value) {
        return value == null ? "N/A" : (value + "");
    }

    private void featureScan(MapLayer<SimpleFeatureType, MemFeature> layer) throws Exception {
        try (var features = layer.featureSource().features()) {
            while (features.hasNext()) {
                var feature = features.next();
                var type = feature.getType().getTypeName();
                counts.put(type, counts.get(type) + 1);
                updateNilCount(type, feature, AT_SCAMIN, nullMinScale);
                updateNilCount(type, feature, AT_SCAMAX, nullMaxScale);
                updateMinMaxes(type, feature, AT_SCAMIN, minScale, Integer.MAX_VALUE, Math::min);
                updateMinMaxes(type, feature, AT_SCAMAX, maxScale, Integer.MIN_VALUE, Math::max);
                updateGeomCounts(feature);
            }
        }
    }

    private void ensureInitialized(Map<String, Integer> map, String property, int initialValue) {
        map.computeIfAbsent(property, k -> initialValue);
    }

    private void updateNilCount(
            String type, MemFeature feature, String property, Map<String, Integer> count) {
        var prop = feature.getProperty(property);
        ensureInitialized(count, type, 0);
        if (prop.getValue() == null) {
            count.put(type, count.get(type) + 1);
        }
    }

    private void updateMinMaxes(
            String type,
            MemFeature feature,
            String property,
            Map<String, Integer> minMaxVals,
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

        pointGeoms.computeIfAbsent(type, k -> 0);
        multiPointGeoms.computeIfAbsent(type, k -> 0);
        lineStringGeoms.computeIfAbsent(type, k -> 0);
        multiLineStringGeoms.computeIfAbsent(type, k -> 0);
        polygonGeoms.computeIfAbsent(type, k -> 0);
        multiPolygonGeoms.computeIfAbsent(type, k -> 0);
        mixedGeoms.computeIfAbsent(type, k -> 0);
        totGeoms.computeIfAbsent(type, k -> 0);

        switch (prop.getName().getLocalPart()) {
            case "Point":
                pointGeoms.put(type, pointGeoms.get(type) + 1);
                break;
            case "MultiPoint":
                multiPointGeoms.put(type, multiPointGeoms.get(type) + 1);
                break;
            case "LineString":
                lineStringGeoms.put(type, lineStringGeoms.get(type) + 1);
                break;
            case "MultiLineString":
                multiLineStringGeoms.put(type, multiLineStringGeoms.get(type) + 1);
                break;
            case "Polygon":
                polygonGeoms.put(type, polygonGeoms.get(type) + 1);
                break;
            case "MultiPolygon":
                multiPolygonGeoms.put(type, multiPolygonGeoms.get(type) + 1);
                break;
            case "GeometryCollection":
                mixedGeoms.put(type, mixedGeoms.get(type) + 1);
                break;
            default:
                throw new IllegalArgumentException();
        }

        totGeoms.put(
                type,
                totGeoms.get(type) + ((Geometry) feature.getDefaultGeometry()).getNumGeometries());
    }
}