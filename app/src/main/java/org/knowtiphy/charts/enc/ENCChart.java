/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import org.geotools.api.feature.simple.SimpleFeatureType;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.model.MapModel;
import org.knowtiphy.shapemap.model.MapViewModel;
import org.knowtiphy.shapemap.model.MapViewport;
import org.knowtiphy.shapemap.renderer.context.RemoveHolesFromPolygon;
import org.knowtiphy.shapemap.renderer.context.RenderGeomCache;
import org.knowtiphy.shapemap.renderer.context.SVGCache;

import java.util.List;

/**
 * An ENC chart -- a map view model for a collection of ENC cells -- a quilt.
 */

public class ENCChart extends MapViewModel<SimpleFeatureType, MemFeature>
{
//  private final List<ENCCell> cells;

  public ENCChart(
    List<MapModel<SimpleFeatureType, MemFeature>> maps, MapViewport viewport, SVGCache svgCache)
  {
    super(maps, viewport, FeatureAdapter.ADAPTER, new RemoveHolesFromPolygon(new RenderGeomCache()),
      svgCache, TextSizeProvider.PROVIDER);
//    this.cells = new ArrayList<>();
  }

  public boolean isQuilt(){return maps().size() > 1;}

//  public ENCCell cell()
//  {
//    return cells.get(0);
//  }

  public int cScale()
  {
    return maps().get(0).cScale();
  }

  public double zoomFactor()
  {
    return bounds().getWidth() / (viewPortBounds().getWidth());
  }

  public double displayScale()
  {
    return (int) (cScale() * (1 / zoomFactor()));
  }

  public double adjustedDisplayScale(){return displayScale() / 2.0;}

  public String title(){return "";} //return maps().get(0).lName();}

}