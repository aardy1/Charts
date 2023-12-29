/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author graham
 */
public class ENCCell
{
  private final Catalog catalog;

  private String name;

  private String lname;

  private int cScale;

  private boolean active;

  private String zipFileLocation;

  private final List<Panel> panels = new ArrayList<>();

  private Path location;

  public ENCCell(Catalog catalog)
  {
    this.catalog = catalog;
  }

  public Catalog catalog(){return catalog;}

  public String name()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String lName()
  {
    return lname;
  }

  public void setLname(String lname)
  {
    this.lname = lname;
  }

  public int cScale()
  {
    return cScale;
  }

  public void setcScale(int scale)
  {
    this.cScale = scale;
  }

  public boolean active()
  {
    return active;
  }

  public void setActive(boolean active)
  {
    this.active = active;
  }

  public String zipFileLocation()
  {
    return zipFileLocation;
  }

  public void setZipFileLocation(String zipFileLocation)
  {
    this.zipFileLocation = zipFileLocation;
  }

  public List<Panel> panels()
  {
    return panels;
  }

  public void addPanel(Panel panel)
  {
    this.panels.add(panel);
  }

  public Path location(){return location;}

  public void setLocation(Path location)
  {
    this.location = location;
  }

  public boolean isLoaded()
  {
    return location.toFile().exists();
  }

  public boolean intersects(Geometry envelope)
  {
    return panels.stream().anyMatch(p -> p.intersects(envelope));
  }

  public MultiPolygon geom()
  {
    var polygons = new ArrayList<Polygon>();
    for(var panel : panels)
    {
      polygons.add(panel.geom());
    }

    return new GeometryFactory().createMultiPolygon(polygons.toArray(new Polygon[0]));
  }

  public ReferencedEnvelope bounds(CoordinateReferenceSystem crs)
  {
    var minX = Double.POSITIVE_INFINITY;
    var minY = Double.POSITIVE_INFINITY;
    var maxX = Double.NEGATIVE_INFINITY;
    var maxY = Double.NEGATIVE_INFINITY;

    for(var panel : panels)
    {
      for(var coordinate : panel.vertices())
      {
        minX = Math.min(minX, coordinate.x);
        minY = Math.min(minY, coordinate.y);
        maxX = Math.max(maxX, coordinate.x);
        maxY = Math.max(maxY, coordinate.y);
      }
    }

    // TODO -- get the CRS from the cell file
    return new ReferencedEnvelope(minX, maxX, minY, maxY, crs);
  }

  //  TODO -- how can we tell if two ENCs are the same?
  @Override
  public boolean equals(Object o)
  {
    if(this == o)
    {
      return true;
    }
    if(o == null || getClass() != o.getClass())
    {
      return false;
    }
    ENCCell encCell = (ENCCell) o;
    return Objects.equals(zipFileLocation, encCell.zipFileLocation);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(zipFileLocation);
  }

  @Override
  public String toString()
  {
    return "ENCCell{" + "location=" + location + ", name='" + name + '\'' + ", lname='" + lname + '\'' + ", cScale=" + cScale + ", zipFileLocation='" + zipFileLocation + '\'' + '}';
  }
}