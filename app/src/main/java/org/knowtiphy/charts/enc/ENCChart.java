/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.model.MapModel;
import org.knowtiphy.shapemap.model.MapViewport;
import org.knowtiphy.shapemap.model.Quilt;
import org.knowtiphy.shapemap.renderer.context.RemoveHolesFromPolygon;
import org.knowtiphy.shapemap.renderer.context.RenderGeomCache;
import org.knowtiphy.shapemap.renderer.context.SVGCache;

import java.util.List;

/**
 * An ENC chart -- a map view model for a collection of ENC cells -- a quilt.
 */

public class ENCChart extends Quilt<SimpleFeatureType, MemFeature>
{
  private final ChartLocker chartLocker;

  public ENCChart(
    List<MapModel<SimpleFeatureType, MemFeature>> maps, MapViewport viewport,
    ChartLocker chartLocker, SVGCache svgCache)
  {
    super(maps, viewport, FeatureAdapter.ADAPTER, new RemoveHolesFromPolygon(new RenderGeomCache()),
      svgCache, TextSizeProvider.PROVIDER);
    this.chartLocker = chartLocker;
  }

  public boolean isQuilt(){return maps().size() > 1;}

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

  @Override
  public void setViewPortBounds(ReferencedEnvelope bounds)
    throws TransformException, NonInvertibleTransformException
  {
    var quilt = chartLocker.loadQuilt(bounds, adjustedDisplayScale());
    System.err.println("--------------------");
    System.err.println("VP bounds change :: quilt size = " + quilt.size());
    for(var map : quilt)
    {
      System.err.println("\tmap " + map.title() + " scale " + map.cScale());
    }

    setMaps(quilt);
    super.setViewPortBounds(bounds);
  }

}