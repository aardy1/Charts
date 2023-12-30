package org.knowtiphy.shapemap.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.api.ISchemaAdapter;
import org.knowtiphy.shapemap.api.ITextSizeProvider;
import org.knowtiphy.shapemap.renderer.context.RemoveHolesFromPolygon;
import org.knowtiphy.shapemap.renderer.context.RenderGeomCache;
import org.reactfx.Change;
import org.reactfx.EventSource;
import org.reactfx.EventStream;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class MapViewModel<S, F>
{
  private final EventSource<Change<Boolean>> layerVisibilityEvent = new EventSource<>();

  private final EventSource<Change<ReferencedEnvelope>> viewPortBoundsEvent = new EventSource<>();

  private final ReferencedEnvelope bounds;

  private final ISchemaAdapter<S, F> schemaAdapter;

  private final IFeatureAdapter<F> featureAdapter;

  private final IRenderablePolygonProvider renderablePolygonProvider;

  private final ISVGProvider svgProvider;

  private final ITextSizeProvider textSizeProvider;

  // possibly shouldnt be here -- but it makes for faster rendering
  private int totalRuleCount;

  private final Map<String, MapLayer<S, F>> layers = new LinkedHashMap<>();

  private final MapViewport viewPort;

  protected MapViewModel(
    ReferencedEnvelope bounds, ISchemaAdapter<S, F> schemaAdapter,
    IFeatureAdapter<F> featureAdapter, IRenderablePolygonProvider renderablePolygonProvider,
    ISVGProvider svgProvider, ITextSizeProvider textSizeProvider)
    throws TransformException, NonInvertibleTransformException, FactoryException
  {
    this.bounds = bounds;
    this.schemaAdapter = schemaAdapter;
    this.featureAdapter = featureAdapter;
    this.renderablePolygonProvider = renderablePolygonProvider;
    this.svgProvider = svgProvider;
    this.textSizeProvider = textSizeProvider;
    this.totalRuleCount = 0;

    viewPort = new MapViewport(bounds, false);
  }

  protected MapViewModel(
    ReferencedEnvelope bounds, ISchemaAdapter<S, F> schemaAdapter,
    IFeatureAdapter<F> featureAdapter, ISVGProvider svgProvider, ITextSizeProvider textSizeProvider)
    throws TransformException, NonInvertibleTransformException, FactoryException
  {
    this(bounds, schemaAdapter, featureAdapter, new RemoveHolesFromPolygon(new RenderGeomCache()),
      svgProvider, textSizeProvider);
  }

  public Collection<MapLayer<S, F>> layers()
  {
    return layers.values();
  }

  public void addLayer(MapLayer<S, F> layer)
  {
    layers.put(schemaAdapter.name(layer.featureSource().getSchema()), layer);
    totalRuleCount += layer.style().rules().size();
  }

  public int totalRuleCount()
  {
    return totalRuleCount;
  }

  public MapLayer<S, F> layer(String type)
  {
    return layers.get(type);
  }

  public void setLayerVisible(String type, boolean visible)
  {
    var layer = layer(type);
    var oldVisible = layer.isVisible();
    layer.setVisible(visible);
    layerVisibilityEvent.push(new Change<>(oldVisible, visible));
  }

  public ReferencedEnvelope bounds()
  {
    return bounds;
  }

  public CoordinateReferenceSystem crs()
  {
    return bounds.getCoordinateReferenceSystem();
  }

  //

  public ReferencedEnvelope viewPortBounds()
  {
    return viewPort.bounds();
  }

  public void setViewPortBounds(ReferencedEnvelope bounds)
    throws TransformException, NonInvertibleTransformException
  {

    var oldBounds = viewPort.bounds();
    viewPort.setBounds(bounds);
    viewPortBoundsEvent.push(new Change<>(oldBounds, bounds));
  }

  public Rectangle2D viewPortScreenArea()
  {
    return viewPort.screenArea();
  }

  public void setViewPortScreenArea(Rectangle2D screenArea)
    throws TransformException, NonInvertibleTransformException
  {
    viewPort.setScreenArea(screenArea);
  }

  public Affine viewPortScreenToWorld()
  {
    return viewPort.screenToWorld();
  }

  public Affine viewPortWorldToScreen()
  {
    return viewPort.worldToScreen();
  }

  //

  public IFeatureAdapter<F> featureAdapter()
  {
    return featureAdapter;
  }

  public IRenderablePolygonProvider renderablePolygonProvider()
  {
    return renderablePolygonProvider;
  }

  public ISVGProvider svgProvider()
  {
    return svgProvider;
  }

  public ITextSizeProvider textSizeProvider()
  {
    return textSizeProvider;
  }

  //

  public EventStream<Change<Boolean>> layerVisibilityEvent()
  {
    return layerVisibilityEvent;
  }

  public EventStream<Change<ReferencedEnvelope>> viewPortBoundsEvent()
  {
    return viewPortBoundsEvent;
  }
}