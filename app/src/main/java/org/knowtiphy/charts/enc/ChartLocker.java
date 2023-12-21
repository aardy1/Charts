/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;

import javax.xml.stream.XMLStreamException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author graham
 */
public class ChartLocker
{
  private final ChartLoader chartLoader;

  private final Path chartsDir;

  private final List<Catalog> availableCatalogs = new ArrayList<>();

  private final ObservableList<ENCCell> history = FXCollections.observableArrayList();

  public ChartLocker(Path chartsDir, ChartLoader chartLoader) throws IOException, XMLStreamException
  {
    this.chartsDir = chartsDir;
    this.chartLoader = chartLoader;

    //  load cached catalogs
    System.err.println("available cats = " + readAvailableCatalogs(chartsDir));
    for(var catalogFile : readAvailableCatalogs(chartsDir))
    {
      var catalog = new CatalogReader(chartsDir, catalogFile).read();
      availableCatalogs.add(catalog);
    }

    System.err.println(availableCatalogs);
  }

  public Collection<ENCCell> intersections(ReferencedEnvelope envelope)
  {
    var bounds = JTS.toGeometry(envelope);

    var result = new ArrayList<ENCCell>();
    for(var catalog : availableCatalogs())
    {
      for(var cell : catalog.activeCells())
      {
        if(cell.intersects(bounds))
        {
          result.add(cell);
        }
      }
    }

    return result;
  }

  public ENCChart getChart(ENCCell chartDescription, MapDisplayOptions displayOptions)
    throws IOException, XMLStreamException, TransformException, FactoryException,
           NonInvertibleTransformException, StyleSyntaxException
  {
    var chart = chartLoader.loadChart(chartDescription, displayOptions);
    addChartHistory(chartDescription);
    return chart;
  }

  public ENCChart loadChart(ENCCell chartDescription, MapDisplayOptions displayOptions)
    throws TransformException, FactoryException, NonInvertibleTransformException,
           StyleSyntaxException
  {
    ENCChart newChart;
    try
    {
      newChart = chartLoader.loadChart(chartDescription, displayOptions);
    }
    catch(IOException | XMLStreamException ex)
    {
      Logger.getLogger(ChartLocker.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }

    // newChart.setViewPortScreenArea(screenArea);
    newChart.setViewPortBounds(newChart.bounds());
    addChartHistory(chartDescription);
    return newChart;
  }

  public ObservableList<ENCCell> history()
  {
    return history;
  }

  private void addChartHistory(ENCCell cell)
  {
    if(!history.contains(cell))
    {
      history.add(cell);
    }
  }

  // TODO -- needs to go away or be smarter
  public ENCCell getCell(String lname, int cscale)
  {
    for(var catalog : availableCatalogs)
    {
      for(var cell : catalog.activeCells())
      {
        if(cell.lName().equals(lname) && cell.cScale() == cscale)
        {
          return cell;
        }
      }
    }

    throw new IllegalArgumentException();
  }

  public Collection<Catalog> availableCatalogs()
  {
    return availableCatalogs;
  }

  //  TODO -- reading it twice is a bit clumsy
  public void addCatalog(URL url) throws IOException, XMLStreamException
  {
    //  read and check for no syntax issues
    var catalog = new CatalogReader(chartsDir, url).read();

    //  read and place in the catalogs directory
    var filePath = chartsDir.resolve(Path.of("../ENC_Catalogs", catalog.title() + ".xml"));
    try(var channel = Channels.newChannel(url.openStream());
      var fileOutputStream = new FileOutputStream(filePath.toFile());
      var fileChannel = fileOutputStream.getChannel())
    {
      fileChannel.transferFrom(channel, 0, Long.MAX_VALUE);
      availableCatalogs.add(catalog);
    }
  }

  public void downloadChart(ENCCell cell, ChartDownloaderNotifier notifier) throws IOException
  {
    ChartDownloader.downloadCell(cell, chartsDir, notifier);
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