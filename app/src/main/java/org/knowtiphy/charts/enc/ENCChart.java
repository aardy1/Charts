/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import javafx.scene.transform.*;
import org.geotools.api.feature.simple.*;
import org.geotools.api.referencing.*;
import org.geotools.api.referencing.crs.*;
import org.geotools.api.referencing.operation.*;
import org.knowtiphy.charts.chartview.*;
import org.knowtiphy.charts.chartview.markicons.*;
import org.knowtiphy.charts.memstore.*;
import org.knowtiphy.shapemap.model.*;
import org.knowtiphy.shapemap.renderer.context.*;

import java.util.*;

/**
 * @author graham
 */
public class ENCChart extends MapViewModel<SimpleFeatureType, MemFeature>
{

  private final ChartDescription chartDescription;

  public ENCChart(
    ChartLocker chartLocker, ChartDescription chartDescription, CoordinateReferenceSystem crs,
    MapDisplayOptions displayOptions)
    throws TransformException, FactoryException, NonInvertibleTransformException
  {

    super(chartDescription.getBounds(crs), SchemaAdapter.ADAPTER, FeatureAdapter.ADAPTER,
      new SVGCache(ResourceLoader.class));
    this.chartDescription = chartDescription;
  }

  public ChartDescription getChartDescription()
  {
    return chartDescription;
  }

  public int cScale()
  {
    return chartDescription.cScale();
  }

  public double getZoomFactor()
  {
    return 1 / (viewPortBounds().getWidth() / bounds().getWidth());
  }

  public int currentScale()
  {
    return (int) (cScale() / getZoomFactor());
  }

  public Collection<Panel> getPanels()
  {
    return chartDescription.getPanels();
  }

  public String title()
  {
    return chartDescription.getName();
  }

}