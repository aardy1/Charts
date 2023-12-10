package org.knowtiphy.charts.desktop;

public enum DistanceUnit
{
  KM("Kilometers"), M("Meters"), NM("Nautical Miles");

  private final String name;

  DistanceUnit(String name)
  {
    this.name = name;
  }

  @Override
  public String toString(){return name;}
}