/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import javafx.geometry.Rectangle2D;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.model.MapViewModel;
import org.knowtiphy.shapemap.renderer.context.SVGCache;

/**
 * An ENC chart -- a map view model for an ENC cell
 */

public class ENCChart extends MapViewModel<SimpleFeatureType, MemFeature>
{
  private final ENCCell cell;

  public ENCChart(ENCCell cell, CoordinateReferenceSystem crs, SVGCache svgCache)
    throws TransformException, FactoryException, NonInvertibleTransformException
  {
    super(cell.bounds(crs), SchemaAdapter.ADAPTER, FeatureAdapter.ADAPTER, svgCache,
      TextSizeProvider.PROVIDER);
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

  public int displayScale()
  {
    return (int) (cScale() * (1 / zoomFactor()));
  }

  //  TODO -- make the "2" tuneable
  public int adjustedDisplayScale()
  {
    return displayScale() / 2;
  }

  public String title(){return cell.lName();}

  public void setViewPortScreenArea(Rectangle2D screenArea)
    throws TransformException, NonInvertibleTransformException
  {
    super.setViewPortScreenArea(screenArea);
  }
}