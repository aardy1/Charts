/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.ontology;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.geotools.Coordinates;
import org.knowtiphy.shapemap.model.MapViewModel;

import static org.knowtiphy.charts.geotools.Coordinates.twoDec;

/**
 * @author graham
 */
public class ENC
{

  // standard ENC has this wierd ass scale 0.25 NM 1:4000

  public static double encScaleNM(double nm)
  {
    return nm * 16000;
  }

  public static double encScaleKM(double km)
  {
    return kmToNM(km) * 16000;
  }

  public static double nmToKM(double nm)
  {
    return nm * 1.852;
  }

  public static double kmToNM(double km)
  {
    return km * 0.53996;
  }

  // I don't think this conversion is correct
  public static <S, F> double encScale(MapViewModel<S, F> map)
    throws TransformException, FactoryException
  {
    var meters = Coordinates.distanceAcross(map.bounds());
    return encScaleKM(meters / 1000);
  }

  public static <S, F> String encScaleText(MapViewModel<S, F> map)
    throws TransformException, FactoryException
  {
    var scale = encScale(map);
    return scale > 1_000_000 ? twoDec(scale / 1_000_000) + "MM" : (twoDec(scale) + "");
  }

}