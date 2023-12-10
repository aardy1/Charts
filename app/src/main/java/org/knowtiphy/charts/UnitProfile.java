/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.desktop.DistanceUnit;
import org.knowtiphy.charts.desktop.SpeedUnit;

import java.text.DecimalFormat;
import java.util.function.DoubleFunction;

/**
 * @author graham
 */
public class UnitProfile
{
  private DistanceUnit distanceUnit;

  private SpeedUnit speedUnit;

  public DoubleFunction<Double> fKnotsToMapUnits = UnitProfile::identity;

  public DoubleFunction<Double> fConvertDistance = x -> x;

  public DoubleFunction<Double> fConvertSpeed = x -> x;

  public DoubleFunction<Double> fConvertDepth = x -> x;

  public DoubleFunction<Double> fConvertTemperature = x -> x;

  public DoubleFunction<Double> fConvertScreenUnit = x -> x;

  public DoubleFunction<String> fLatLongString = x -> x + "";

  public double convertDistance(double d)
  {
    return fConvertDistance.apply(d);
  }

  public double convertSpeed(double d)
  {
    return fConvertSpeed.apply(d);
  }

  // e.g. if screen unit is inches, 1cm : 100km -> 1in : 254km
  public double convertFromScreenUnit(double d)
  {
    return fConvertScreenUnit.apply(d);
  }

  public static String labelLongitude(double value)
  {
    var df = new DecimalFormat("###.#\u00B0");
    return df.format(Math.abs(value)) + (value < 0 ? "W" : "E");
  }

  public String labelLattitude(double value)
  {
    var df = new DecimalFormat("###.#\u00B0");
    return df.format(Math.abs(value)) + (value < 0 ? "S" : "N");
  }

  public String envelopeLabel(ReferencedEnvelope bounds)
  {
    return labelLongitude(bounds.getMinX()) + "-" + labelLongitude(
      bounds.getMaxX()) + "   " + labelLattitude(bounds.getMinY()) + "-" + labelLattitude(
      bounds.getMaxY());
  }

  public DistanceUnit distanceUnit(){return distanceUnit;}

  public void updateDistanceUnit(DistanceUnit newDistanceUnit)
  {
    this.distanceUnit = newDistanceUnit;
//    switch(newDistanceUnit)
//    {
//      case KM -> ;
//      case M -> ;
//      case
//    }
  }

  public SpeedUnit speedUnit(){return speedUnit;}

  public void updateSpeedUnit(SpeedUnit newSpeedUnit)
  {
    System.err.println("Changing speed unit to " + newSpeedUnit);
    this.speedUnit = newSpeedUnit;
    switch(newSpeedUnit)
    {
      case KPH -> fKnotsToMapUnits = UnitProfile::knotsToKph;
      case KNOTS -> fKnotsToMapUnits = UnitProfile::identity;
    }
  }

  //  actual conversion functions

  private static <T> T identity(T value){return value;}

  private static double knotsToKph(double value){return value * 1.852;}

}