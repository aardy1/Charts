package org.knowtiphy.charts.settings;

public enum DistanceUnit
{
  KM("km"), M("m"), NM("NM");

  private final String name;

  DistanceUnit(String name)
  {
    this.name = name;
  }

  @Override
  public String toString(){return name;}
}