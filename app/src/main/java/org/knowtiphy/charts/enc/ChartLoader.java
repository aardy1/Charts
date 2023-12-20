/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.charts.memstore.MapStats;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.memstore.StyleReader;
import org.knowtiphy.charts.settings.AppSettings;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A loader of charts from the local chart cache.
 */

public class ChartLoader
{
  private final StyleReader<SimpleFeatureType, MemFeature> styleReader;

  private final List<Catalog> availableCatalogs = new ArrayList<>();

//  private final List<ChartDescription> cells = new ArrayList<>();

  private final AppSettings settings;

  public ChartLoader(
    Path chartDir, AppSettings settings, StyleReader<SimpleFeatureType, MemFeature> styleReader)
    throws IOException, XMLStreamException
  {
    this.styleReader = styleReader;
    this.settings = settings;

    //  load the local catalogs
    var catalogFiles = readAvailableCatalogs(chartDir);
    System.err.println("AC = " + catalogFiles);

    //  load the local chart descriptions
    for(var catalogFile : catalogFiles)
    {
      System.err.println("CF = " + catalogFile);
      var catalog = new CatalogReader(catalogFile).read();
      availableCatalogs.add(catalog);
//      var parent = catalogFile.getParent();
//      for(var cell : catalog.getCells())
//      {
//        var dir = parent.resolve(
//          cell.getName().replaceAll(" ", "_").replaceAll(",", "_") + "_" + cell.getcScale());
//        cells.add(new ChartDescription(dir, cell));
//      }
    }
  }

  // TODO -- needs to go away or be smarter
  public ENCCell getChartDescription(String lname, int cscale)
  {
    for(var catalog : availableCatalogs)
    {
      for(var cell : catalog.getCells())
      {
        if(cell.getLname().equals(lname) && cell.cScale() == cscale)
        {
          return cell;
        }
      }
    }

    return null;
  }

//  public List<ChartDescription> getChartDescriptions()
//  {
//    return cells;
//  }

  public ENCChart loadChart(ENCCell cell, MapDisplayOptions displayOptions)
    throws IOException, XMLStreamException, TransformException, FactoryException,
           NonInvertibleTransformException, StyleSyntaxException
  {
    var reader = new ChartBuilder(cell.shapeFileDir(), cell, settings, styleReader,
      displayOptions).read();
    var map = reader.getMap();
    map.setViewPortBounds(map.bounds());

    var stats = new MapStats(map, SchemaAdapter.ADAPTER).stats();
    stats.print();

    return map;
  }

  public ENCChart loadChart(
    ENCCell cell, ReferencedEnvelope bounds, MapDisplayOptions displayOptions)
    throws IOException, XMLStreamException, TransformException, FactoryException,
           NonInvertibleTransformException, StyleSyntaxException
  {
    var reader = new ChartBuilder(cell.shapeFileDir(), cell, settings, styleReader,
      displayOptions).read();
    var map = reader.getMap();
    map.setViewPortBounds(bounds);
    return map;
  }

  public Collection<Catalog> availableCatalogs()
  {
    return availableCatalogs;
  }

  private static Collection<Path> readAvailableCatalogs(Path chartDir) throws IOException
  {
    var catalogFiles = new ArrayList<Path>();

    try(var walker = Files.walk(chartDir))
    {
      var directories = walker.filter(Files::isDirectory).toList();
      for(var subDir : directories)
      {
        try(var files = Files.find(subDir, Integer.MAX_VALUE,
          (path, basicFileAttributes) -> path.toFile().getName().matches(".*_ENCProdCat.xml")))
        {
          catalogFiles.addAll(files.toList());
        }
      }
    }

    return catalogFiles;
  }

  public Catalog readCatalog(URL url)
  {
    try
    {
      return new CatalogReader(url).read();
    }
    catch(IOException | XMLStreamException e)
    {
      throw new RuntimeException(e);
    }
  }

  public void addCatalog(Catalog catalog)
  {
    availableCatalogs.add(catalog);
//    for(var cell : catalog.getCells())
//    {
////        var dir = parent.resolve(
////          cell.getName().replaceAll(" ", "_").replaceAll(",", "_") + "_" + cell.getcScale());
//      cells.add(new ChartDescription(Path.of("."), cell));
//    }
  }
}