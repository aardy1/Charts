package org.knowtiphy.charts.desktop;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AppSettings
{
  //  settings for units

  public final ObjectProperty<DistanceUnit> distanceUnit = new SimpleObjectProperty<>(
    DistanceUnit.KM);
  public final IntegerProperty distanceUnitDecimals = new SimpleIntegerProperty(2);

  public final ObjectProperty<SpeedUnit> speedUnit = new SimpleObjectProperty<>(SpeedUnit.KNOTS);
  public final IntegerProperty speedUnitDecimals = new SimpleIntegerProperty(2);

  public final ObjectProperty<DepthUnit> depthUnit = new SimpleObjectProperty<>(DepthUnit.M);
  public final IntegerProperty depthUnitDecimals = new SimpleIntegerProperty(2);

  public final ObjectProperty<TemperatureUnit> temperatureUnit = new SimpleObjectProperty<>(
    TemperatureUnit.C);
  public final IntegerProperty temperatureUnitDecimals = new SimpleIntegerProperty(2);

  public final ObjectProperty<LatLongFormat> latLongFormat = new SimpleObjectProperty<>(
    LatLongFormat.DECIMAL_DEGREES);

  //  settings for AIS

  public final DoubleProperty cogPredictorLengthMin = new SimpleDoubleProperty(5);
}