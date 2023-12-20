/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Geometry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author graham
 */
public class ENCCell
{
  private final Path root;

  private String name;

  private String lname;

  private int cScale;

  private String zipFileLocation;

  private final List<Panel> panels = new ArrayList<>();

  public ENCCell(Path root)
  {
    this.root = root;
  }

  public Path shapeFileDir(){return root.resolve(name + "_" + cScale);}

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getLname()
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

  public List<Panel> getPanels()
  {
    return panels;
  }

  public void addPanel(Panel panel)
  {
    this.panels.add(panel);
  }

  public String getZipFileLocation()
  {
    return zipFileLocation;
  }

  public void setZipFileLocation(String zipFileLocation)
  {
    this.zipFileLocation = zipFileLocation;
  }

  public boolean intersects(Geometry envelope)
  {
    return panels.stream().anyMatch(p -> p.intersects(envelope));
  }

  public ReferencedEnvelope getBounds(CoordinateReferenceSystem crs)
  {

    var minX = Double.POSITIVE_INFINITY;
    var minY = Double.POSITIVE_INFINITY;
    var maxX = Double.NEGATIVE_INFINITY;
    var maxY = Double.NEGATIVE_INFINITY;

    for(var panel : panels)
    {
      for(var coordinate : panel.getVertices())
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

  @Override
  public String toString()
  {
    return "ENCCell{" + "root=" + root + ", name='" + name + '\'' + ", lname='" + lname + '\'' +
             ", cScale=" + cScale + ", zipFileLocation='" + zipFileLocation + '\'' + ", panels=" + panels + '}';
  }
}