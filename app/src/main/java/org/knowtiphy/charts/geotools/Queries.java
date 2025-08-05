/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.geotools;

/**
 * @author graham
 */
public class Queries {
    private static final double DELTA = 0.1;

    //  public static <S, F> List<SimpleFeatureCollection> featuresNearXYWorld(
    //    MapViewModel<S, F> map, double x, double y) throws IOException
    //  {
    //
    //    var tx = new Transformation(map.viewPortScreenToWorld());
    //    tx.apply(x, y);
    //    var envelope = new ReferencedEnvelope(tx.getX() - DELTA, tx.getX() + DELTA, tx.getY() -
    // DELTA,
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
    //
    //    public static <S, F> List<IFeatureSourceIterator<F>> featuresNearXYWorld(
    //            BaseMapViewModel<S, F> mapViewModel, double x, double y, int radius) throws
    // Exception {
    //
    //        var envelope = tinyPolygon(mapViewModel, x, y, radius);
    //
    //        var result = new ArrayList<IFeatureSourceIterator<F>>();
    //
    //        for (var map : mapViewModel.maps()) {
    //            for (var layer : map.layers()) {
    //                result.add(layer.featureSource().features(envelope, Double.MIN_VALUE, true));
    //            }
    //        }
    //
    //        return result;
    //    }
}