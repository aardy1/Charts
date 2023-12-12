/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.settings;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.utils.Utils;
import org.reactfx.EventSource;
import org.reactfx.EventStream;

import java.text.DecimalFormat;
import java.util.function.DoubleFunction;
import java.util.function.UnaryOperator;

import static org.knowtiphy.charts.settings.SpeedUnit.KNOTS;

/**
 * @author graham
 */
public class UnitProfile
{
  private EventSource<Boolean> unitChangeEvents = new EventSource<>();

  public final ObjectProperty<SpeedUnit> speedUnit = new SimpleObjectProperty<>(KNOTS);

  public final IntegerProperty speedUnitDecimals = new SimpleIntegerProperty(2);

  public final ObjectProperty<DistanceUnit> distanceUnit = new SimpleObjectProperty<>(
    DistanceUnit.KM);
  public final IntegerProperty distanceUnitDecimals = new SimpleIntegerProperty(2);

  public final ObjectProperty<DepthUnit> depthUnit = new SimpleObjectProperty<>(DepthUnit.M);
  public final IntegerProperty depthUnitDecimals = new SimpleIntegerProperty(2);

  public final ObjectProperty<TemperatureUnit> temperatureUnit = new SimpleObjectProperty<>(
    TemperatureUnit.C);
  public final IntegerProperty temperatureUnitDecimals = new SimpleIntegerProperty(2);

  public final ObjectProperty<LatLongFormat> latLongFormat = new SimpleObjectProperty<>(
    LatLongFormat.DECIMAL_DEGREES);

  public DoubleFunction<Double> fConvertScreenUnit = x -> x;

  public UnitProfile()
  {
    speedUnit.addListener((observable, oldValue, newValue) -> unitChangeEvents.push(true));
    speedUnitDecimals.addListener((observable, oldValue, newValue) -> unitChangeEvents.push(true));
    distanceUnit.addListener((observable, oldValue, newValue) -> unitChangeEvents.push(true));
    distanceUnitDecimals.addListener((observable, oldValue, newValue) -> {
      System.err.println("YYYYY");

      unitChangeEvents.push(true);
    });
    depthUnit.addListener((observable, oldValue, newValue) -> unitChangeEvents.push(true));
    depthUnitDecimals.addListener((observable, oldValue, newValue) -> unitChangeEvents.push(true));
    temperatureUnit.addListener((observable, oldValue, newValue) -> unitChangeEvents.push(true));
    temperatureUnitDecimals.addListener(
      (observable, oldValue, newValue) -> unitChangeEvents.push(true));
  }

  public EventStream<Boolean> unitChangeEvents()
  {
    return unitChangeEvents;
  }

  public String formatDistance(Number value, UnaryOperator<Number> converter)
  {
    System.err.println("dist unit dec = " + distanceUnitDecimals.getValue());
    return Utils.formatDecimal(converter.apply(value), distanceUnitDecimals.getValue());
  }

  public String formatSpeed(Number value, UnaryOperator<Number> converter)
  {
    return Utils.formatDecimal(converter.apply(value), speedUnitDecimals.getValue());
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

  public DistanceUnit distanceUnit(){return distanceUnit.get();}

  //  actual conversion functions

  public Number metersToMapUnits(Number value)
  {
    return switch(distanceUnit.get())
    {
      case KM -> value.doubleValue() / 1000;
      case M -> value;
      case NM -> value.doubleValue() * 0.0005399568;
    };
  }

  public Number knotsToMapUnits(Number value)
  {
    return switch(speedUnit.get())
    {
      case KPH -> value.doubleValue() * 1.852;
      case KNOTS -> value;
    };
  }

  private static <T> T identity(T value){return value;}

  private static double knotsToKph(double value){return value * 1.852;}

}