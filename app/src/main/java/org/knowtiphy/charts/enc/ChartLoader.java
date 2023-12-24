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
import org.knowtiphy.shapemap.renderer.context.SVGCache;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * A loader of charts from the local chart cache.
 */

public class ChartLoader
{
  private final StyleReader<SimpleFeatureType, MemFeature> styleReader;

  private final AppSettings settings;

  public ChartLoader(AppSettings settings, StyleReader<SimpleFeatureType, MemFeature> styleReader)
  {
    this.styleReader = styleReader;
    this.settings = settings;
  }

  ENCChart loadChart(ENCCell cell, MapDisplayOptions displayOptions, SVGCache svgCache)
    throws IOException, XMLStreamException, TransformException, FactoryException,
           NonInvertibleTransformException, StyleSyntaxException
  {
    var reader = new ChartBuilder(cell, settings, styleReader, displayOptions, svgCache).read();
    var map = reader.getMap();
    map.setViewPortBounds(map.bounds());

    var stats = new MapStats(map, SchemaAdapter.ADAPTER).stats();
    stats.print();

    return map;
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