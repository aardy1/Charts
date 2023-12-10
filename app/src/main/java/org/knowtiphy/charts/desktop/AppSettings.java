package org.knowtiphy.charts.desktop;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AppSettings
{
  //  settings for units

  public final ObjectProperty<DistanceUnit> distanceUnit = new SimpleObjectProperty<>(
    DistanceUnit.KM);
  public final ObjectProperty<SpeedUnit> speedUnit = new SimpleObjectProperty<>(SpeedUnit.KPH);
  public final ObjectProperty<DepthUnit> depthUnit = new SimpleObjectProperty<>(DepthUnit.M);
  public final ObjectProperty<TemperatureUnit> temperatureUnit = new SimpleObjectProperty<>(
    TemperatureUnit.C);
  public final ObjectProperty<LatLongFormat> latLongFormat = new SimpleObjectProperty<>(
    LatLongFormat.DECIMAL_DEGREES);

  //  settings for AIS

  public final DoubleProperty cogPredictorLengthMin = new SimpleDoubleProperty(5);
}