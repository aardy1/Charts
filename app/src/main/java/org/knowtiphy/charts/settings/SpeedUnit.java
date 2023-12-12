package org.knowtiphy.charts.settings;

public enum SpeedUnit
{
  KPH("Kph"), KNOTS("Knots");

  private final String name;

  SpeedUnit(String name)
  {
    this.name = name;
  }

  @Override
  public String toString(){return name;}
}