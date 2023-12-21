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

/**
 * An ENC chart -- a map view model for an ENC cell
 */

public class ENCChart extends MapViewModel<SimpleFeatureType, MemFeature>
{
  private final ENCCell cell;

  public ENCChart(ENCCell cell, CoordinateReferenceSystem crs)
    throws TransformException, FactoryException, NonInvertibleTransformException
  {
    super(cell.bounds(crs), SchemaAdapter.ADAPTER, FeatureAdapter.ADAPTER,
      new SVGCache(ResourceLoader.class), TextSizeProvider.PROVIDER);
    this.cell = cell;
  }

  public ENCCell cell()
  {
    return cell;
  }

  public int cScale()
  {
    return cell.cScale();
  }

  public double zoomFactor()
  {
    return 1 / (viewPortBounds().getWidth() / bounds().getWidth());
  }

  public int currentScale()
  {
    return (int) (cScale() / zoomFactor());
  }

  public String title(){return cell.lName();}
}