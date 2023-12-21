/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
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
import java.util.stream.Stream;

/**
 * A loader of charts from the local chart cache.
 */

public class ChartLoader
{
  private final StyleReader<SimpleFeatureType, MemFeature> styleReader;

  private final List<Catalog> availableCatalogs = new ArrayList<>();

  private final Path chartDir;

  private final AppSettings settings;

  public ChartLoader(
    Path chartDir, AppSettings settings, StyleReader<SimpleFeatureType, MemFeature> styleReader)
    throws IOException, XMLStreamException
  {
    this.chartDir = chartDir;
    this.styleReader = styleReader;
    this.settings = settings;

    //  load the local catalogs
    System.err.println("available cats = " + readAvailableCatalogs(chartDir));
    for(var catalogFile : readAvailableCatalogs(chartDir))
    {
      var catalog = new CatalogReader(chartDir, catalogFile).read();
      availableCatalogs.add(catalog);
    }

    System.err.println(availableCatalogs);
  }

  // TODO -- needs to go away or be smarter
  public ENCCell getCell(String lname, int cscale)
  {
    for(var catalog : availableCatalogs)
    {
      for(var cell : catalog.cells())
      {
        if(cell.lName().equals(lname) && cell.cScale() == cscale)
        {
          return cell;
        }
      }
    }

    throw new IllegalArgumentException();
  }

  public ENCChart loadChart(ENCCell cell, MapDisplayOptions displayOptions)
    throws IOException, XMLStreamException, TransformException, FactoryException,
           NonInvertibleTransformException, StyleSyntaxException
  {
    var reader = new ChartBuilder(cell, settings, styleReader, displayOptions).read();
    var map = reader.getMap();
    map.setViewPortBounds(map.bounds());

    var stats = new MapStats(map, SchemaAdapter.ADAPTER).stats();
    stats.print();

    return map;
  }

  public Collection<Catalog> availableCatalogs()
  {
    return availableCatalogs;
  }

  public Catalog readCatalog(URL url) throws IOException, XMLStreamException
  {
    return new CatalogReader(chartDir, url).read();
  }

  public void addCatalog(Catalog catalog)
  {
    for(var cat : availableCatalogs)
    {
      if(!cat.title().equals(catalog.title()))
      {
        availableCatalogs.add(catalog);
      }
    }
  }

  private static Collection<Path> readAvailableCatalogs(Path chartDir) throws IOException
  {
    //  TODO -- fix this
    try(Stream<Path> stream = Files.list(chartDir.resolve("../ENC_Catalogs")))
    {
      return stream.filter(
        file -> !Files.isDirectory(file) && !file.toFile().getName().equals(".DS_Store")).toList();
    }
  }

}

// try(var walker = Files.walk(chartDir))
//                                        {
//var directories = walker.filter(Files::isDirectory).toList();
//      for(var subDir : directories)
//                                    {
//                                    try(var files = Files.find(subDir, Integer.MAX_VALUE,
//  (path, basicFileAttributes) -> path.toFile().getName().matches(".*_ENCProdCat.xml")))
//                                                                                        {
//                                                                                        catalogFiles.addAll(files.toList());
//                                                                                                                            }
//                                                                                                                            }
//                                                                                                                            }
//  public ENCChart loadChart(
//    ENCCell cell, ReferencedEnvelope bounds, MapDisplayOptions displayOptions)
//    throws IOException, XMLStreamException, TransformException, FactoryException,
//           NonInvertibleTransformException, StyleSyntaxException
//  {
//    var reader = new ChartBuilder(cell.shapeFileDir(), cell, settings, styleReader,
//      displayOptions).read();
//    var map = reader.getMap();
//    map.setViewPortBounds(bounds);
//    return map;
//  }