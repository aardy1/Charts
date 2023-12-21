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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author graham
 */
public class ChartLocker
{
  private final ChartLoader chartLoader;

  private final ObservableList<ENCCell> history = FXCollections.observableArrayList();

  public ChartLocker(ChartLoader chartLoader)
  {
    this.chartLoader = chartLoader;
  }

  public Collection<ENCCell> intersections(ReferencedEnvelope envelope)
  {

    var bounds = JTS.toGeometry(envelope);

    var result = new ArrayList<ENCCell>();
    for(var catalog : chartLoader.availableCatalogs())
    {
      for(var cell : catalog.cells())
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

  public ChartLoader chartLoader()
  {
    return chartLoader;
  }

  public ObservableList<ENCCell> history()
  {
    return history;
  }

  private void addChartHistory(ENCCell chartDescription)
  {
    if(!history.contains(chartDescription))
    {
      history.add(chartDescription);
    }
  }
}