/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import org.geotools.api.feature.simple.SimpleFeatureType;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.charts.ontology.ENC;
import org.knowtiphy.shapemap.api.ISchemaAdapter;
import org.knowtiphy.shapemap.model.MapLayer;
import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntBinaryOperator;

import static org.knowtiphy.charts.geotools.Coordinates.distanceAcross;
import static org.knowtiphy.charts.ontology.S57.AT_SCAMAX;
import static org.knowtiphy.charts.ontology.S57.AT_SCAMIN;

/**
 * @author graham
 */
public class MapStats
{
  private final ENCChart map;

  private final ISchemaAdapter<SimpleFeatureType, MemFeature> adapter;

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

  public MapStats(ENCChart map, ISchemaAdapter<SimpleFeatureType, MemFeature> adapter)
  {
    this.map = map;
    this.adapter = adapter;
  }

  public MapStats stats()
  {
    try
    {
      for(var layer : map.layers())
      {
        var layerSize = ((MemStoreFeatureSource) layer.getFeatureSource()).size();
        var type = adapter.name(layer.getFeatureSource().getSchema());
        counts.put(type, layerSize);
        featureScan(layer, type);
      }
    }
    catch(Exception ex)
    {
      //  ignore
    }

    return this;
  }

  public void print()
  {
    try
    {
      var numFeatures = 0;
      for(var layer : map.layers())
      {
        var layerSize = ((MemStoreFeatureSource) layer.getFeatureSource()).size();
        numFeatures += layerSize;
      }

      var numGeoms = 0;
      for(var value : totGeoms.values())
      {
        numGeoms += value;
      }

      var keys = new ArrayList<>(counts.keySet());
      keys.sort(String::compareTo);

      System.err.println();
      System.err.println("Scale Summary");
      System.err.printf("%-8s %-7s %-12s %-12s %-10s %-10s%n", "type", "#", "#N-SCAMIN",
        "#N-SCAMAX", "SCAMIN", "SCAMAX");
      for(var key : keys)
      {
        System.err.printf("%-8s %-7d %-12s %-12s %-10s %-10s%n", key, counts.get(key),
          N(nullMinScale.get(key)), N(nullMaxScale.get(key)), N(minScale.get(key)),
          N(maxScale.get(key)));
      }

      System.err.println();
      System.err.println("Geometry Summary");
      System.err.printf("%-8s  %-7s %-8s %-8s %-8s %-8s %-8s %-8s %-8s %-8s%n", "type", "#", "#Pt",
        "#Line", "#Poly", "#M-Pt", "#M-Line", "#M-Poly", "#Mixed", "Tot Geoms");
      for(var key : keys)
      {
        System.err.printf("%-8s  %-7d %-8s %-8s %-8s %-8s %-8s %-8s %-8s %-8s%n", key,
          counts.get(key), pointGeoms.get(key), lineStringGeoms.get(key), polygonGeoms.get(key),
          multiPointGeoms.get(key), multiLineStringGeoms.get(key), multiPolygonGeoms.get(key),
          mixedGeoms.get(key), totGeoms.get(key));
      }

      System.err.println();
      System.err.println("Total num features = " + numFeatures);
      System.err.println("Total geoms = " + numGeoms);
      System.err.println();

      var mapSpans = distanceAcross(map) / 1000;
      System.err.println("Map span = " + mapSpans + " km");
      System.err.println("Map span = " + ENC.kmToNM(mapSpans) + " nm");
      System.err.println();

      System.err.println("Map title " + map.title());

      System.err.println();
    }
    catch(Exception ex)
    {
      //  ignore
    }
  }

  public Map<String, Integer> getMinScale()
  {
    return minScale;
  }

  public Map<String, Integer> getMaxScale()
  {
    return maxScale;
  }

  private String N(Integer value)
  {
    return value == null ? "N/A" : (value + "");
  }

  private void featureScan(MapLayer<SimpleFeatureType, MemFeature> layer, String type)
    throws Exception
  {
    try(var features = layer.getFeatureSource().features())
    {
      while(features.hasNext())
      {
        var feature = features.next();
        updateNilCount(type, feature, AT_SCAMIN, nullMinScale);
        updateNilCount(type, feature, AT_SCAMAX, nullMaxScale);
        updateMinMaxes(type, feature, AT_SCAMIN, minScale, Integer.MAX_VALUE, Math::min);
        updateMinMaxes(type, feature, AT_SCAMAX, maxScale, Integer.MIN_VALUE, Math::max);
        updateGeomCounts(type, feature);
      }
    }
  }

  private void ensureInitialized(
    Map<String, Integer> map, String property, int initialValue)
  {
    map.computeIfAbsent(property, k -> initialValue);
  }

  private void updateNilCount(
    String type, MemFeature feature, String property, Map<String, Integer> count)
  {
    var prop = feature.getProperty(property);
    ensureInitialized(count, type, 0);
    if(prop.getValue() == null)
    {
      count.put(type, count.get(type) + 1);
    }
  }

  private void updateMinMaxes(
    String type, MemFeature feature, String property, Map<String, Integer> minMaxVals,
    int initialValue, IntBinaryOperator nextVal)
  {
    var prop = feature.getProperty(property);
    if(prop.getValue() != null)
    {
      ensureInitialized(minMaxVals, type, initialValue);
      minMaxVals.put(type, nextVal.applyAsInt(minMaxVals.get(type), (int) prop.getValue()));
    }
  }

  private void updateGeomCounts(String type, MemFeature feature)
  {
    var prop = feature.getDefaultGeometryProperty().getType();

    pointGeoms.computeIfAbsent(type, k -> 0);
    multiPointGeoms.computeIfAbsent(type, k -> 0);
    lineStringGeoms.computeIfAbsent(type, k -> 0);
    multiLineStringGeoms.computeIfAbsent(type, k -> 0);
    polygonGeoms.computeIfAbsent(type, k -> 0);
    multiPolygonGeoms.computeIfAbsent(type, k -> 0);
    mixedGeoms.computeIfAbsent(type, k -> 0);
    totGeoms.computeIfAbsent(type, k -> 0);

    switch(prop.getName().getLocalPart())
    {
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

    totGeoms.put(type,
      totGeoms.get(type) + ((Geometry) feature.getDefaultGeometry()).getNumGeometries());
  }
}