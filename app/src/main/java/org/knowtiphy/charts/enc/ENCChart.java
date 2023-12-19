/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.chartview.markicons.ResourceLoader;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.model.MapViewModel;
import org.knowtiphy.shapemap.renderer.context.SVGCache;

import java.util.Collection;

/**
 * @author graham
 */
public class ENCChart extends MapViewModel<SimpleFeatureType, MemFeature>
{
  private final ChartDescription chartDescription;

  public ENCChart(ChartDescription chartDescription, CoordinateReferenceSystem crs)
    throws TransformException, FactoryException, NonInvertibleTransformException
  {
    super(chartDescription.getBounds(crs), SchemaAdapter.ADAPTER, FeatureAdapter.ADAPTER,
      new SVGCache(ResourceLoader.class), TextSizeProvider.PROVIDER);
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