package org.knowtiphy.charts.settings;

public enum TemperatureUnit
{
  C("Celsius"), F("Fahrenheit");

  final String name;

  TemperatureUnit(String name)
  {
    this.name = name;
  }

  @Override
  public String toString(){return name;}
}