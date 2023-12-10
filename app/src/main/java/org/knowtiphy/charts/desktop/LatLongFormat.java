package org.knowtiphy.charts.desktop;

public enum LatLongFormat
{
  DECIMAL_DEGREES("Decimal Degrees"), DEGREES_MINUTES_SECONDS("Degrees, Minutes, Seconds");

  private final String name;

  LatLongFormat(String name)
  {
    this.name = name;
  }

  @Override
  public String toString(){return name;}
}