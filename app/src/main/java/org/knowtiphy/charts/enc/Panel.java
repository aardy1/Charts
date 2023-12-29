/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * @author graham
 */
public class Panel
{

  private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

  // Simply pass an array of Coordinate or a CoordinateSequence to its method
  private int panelNumber;

  // the boundary points of the panel
  private final List<Coordinate> vertices = new ArrayList<>();

  private Polygon geom;

  public int panelNumber()
  {
    return panelNumber;
  }

  public void setPanelNumber(int panelNumber)
  {
    this.panelNumber = panelNumber;
  }

  public List<Coordinate> vertices()
  {
    return vertices;
  }

  public void addVertex(Coordinate vertex)
  {
    this.vertices.add(vertex);
  }

  public void createGeom()
  {
    var pts = new Coordinate[vertices.size()];
    for(var i = 0; i < vertices.size(); i++)
    {
      pts[i] = vertices.get(i);
    }

    geom = GEOMETRY_FACTORY.createPolygon(pts);
  }

  public Polygon geom()
  {
    return geom;
  }

  public boolean intersects(Geometry envelope)
  {
    return envelope.intersects(geom);
  }

  @Override
  public String toString()
  {
    return "Panel{" + "panelNumber=" + panelNumber + ", vertices=" + vertices + '}';
  }

}