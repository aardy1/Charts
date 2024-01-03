/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.geotools;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeatureSourceIterator;
import org.knowtiphy.shapemap.model.MapViewModel;
import org.knowtiphy.shapemap.renderer.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author graham
 */
public class Queries
{
  private static final double DELTA = 0.1;

//  public static <S, F> List<SimpleFeatureCollection> featuresNearXYWorld(
//    MapViewModel<S, F> map, double x, double y) throws IOException
//  {
//
//    var tx = new Transformation(map.viewPortScreenToWorld());
//    tx.apply(x, y);
//    var envelope = new ReferencedEnvelope(tx.getX() - DELTA, tx.getX() + DELTA, tx.getY() - DELTA,
//      tx.getY() + DELTA, map.crs());
//
//    var result = new ArrayList<SimpleFeatureCollection>();
//    for(var layer : map.layers())
//    {
//      result.add((SimpleFeatureCollection) layer.featureSource().features(envelope, true));
//    }
//
//    return result;
//  }

  public static <S, F> List<IFeatureSourceIterator<F>> featuresNearXYWorld(
    MapViewModel<S, F> mapViewModel, double x, double y, int radius) throws Exception
  {

    var envelope = tinyPolygon(mapViewModel, x, y, radius);

    var result = new ArrayList<IFeatureSourceIterator<F>>();

    for(var map : mapViewModel.maps())
    {
      for(var layer : map.layers())
      {
        result.add(layer.featureSource().features(envelope, Double.MIN_VALUE, true));
      }
    }

    return result;
  }

  public static <S, F> ReferencedEnvelope tinyPolygon(
    MapViewModel<S, F> map, double x, double y, int radius)
  {
    int screenMinX = (int) x - radius;
    int screenMinY = (int) y - radius;
    int screenMaxX = (int) x + radius;
    int screenMaxY = (int) y + radius;
    /*
     * Transform the screen rectangle into bounding box in the coordinate reference
     * system of our map context. Note: we are using a naive method here but GeoTools
     * also offers other, more accurate methods.
     */
    Transformation tx = new Transformation(map.viewPortScreenToWorld());
    tx.apply(screenMinX, screenMinY);
    double minX = tx.getX();
    double minY = tx.getY();
    tx.apply(screenMaxX, screenMaxY);
    double maxX = tx.getX();
    double maxY = tx.getY();
    double width = maxX - minX;
    double height = maxY - minY;
    // TODO -- fix this as upside down
    return new ReferencedEnvelope(minX, minX + width, minY, minY + height, map.crs());
  }

  public static <S, F> ReferencedEnvelope tinyPolygon(MapViewModel<S, F> map, double x, double y)
  {
    return tinyPolygon(map, x, y, 1);
  }

}