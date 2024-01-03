package org.knowtiphy.shapemap.model;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.ISchemaAdapter;
import org.locationtech.jts.geom.Geometry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A map model -- map bounds and compilation scale, and a collection of map layers.
 *
 * @param <S> the type of the schema for the map layers
 * @param <F> the type of the features for the map layers
 */

public class MapModel<S, F>
{
  private final ReferencedEnvelope bounds;

  private Geometry geometry;

  private final int cScale;

  private final String title;

  private final ISchemaAdapter<S, F> schemaAdapter;

  private final Map<String, MapLayer<S, F>> layers = new LinkedHashMap<>();

  // possibly shouldnt be here -- but it makes for faster rendering
  private int totalRuleCount = 0;

  public MapModel(
    ReferencedEnvelope bounds, int cScale, String title, ISchemaAdapter<S, F> schemaAdapter)
  {
    this.bounds = bounds;
    this.cScale = cScale;
    this.title = title;
    this.schemaAdapter = schemaAdapter;
  }

  public ReferencedEnvelope bounds()
  {
    return bounds;
  }

  public int cScale()
  {
    return cScale;
  }

  public String title()
  {
    return title;
  }

  public Geometry geometry()
  {
    return geometry;
  }

  public void setGeometry(Geometry geometry)
  {
    this.geometry = geometry;
  }

  public Collection<MapLayer<S, F>> layers()
  {
    return layers.values();
  }

  public void addLayer(MapLayer<S, F> layer)
  {
    layers.put(schemaAdapter.name(layer.featureSource().getSchema()), layer);
    totalRuleCount += layer.style().rules().size();
    //  TODO -- publish an add layer event
  }

  public MapLayer<S, F> layer(String type)
  {
    return layers.get(type);
  }

  public int totalRuleCount()
  {
    return totalRuleCount;
  }

  public CoordinateReferenceSystem crs()
  {
    return bounds.getCoordinateReferenceSystem();
  }
}