package org.knowtiphy.charts.desktop;

public enum DepthUnit
{
  M("Meters"), FEET("Feet"), FATHOM("Fathoms");

  private final String name;

  DepthUnit(String name)
  {
    this.name = name;
  }

  @Override
  public String toString(){return name;}
}