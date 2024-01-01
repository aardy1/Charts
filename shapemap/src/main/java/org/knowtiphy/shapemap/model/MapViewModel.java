package org.knowtiphy.shapemap.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.api.ITextSizeProvider;
import org.reactfx.Change;
import org.reactfx.EventSource;
import org.reactfx.EventStream;

import java.util.Collection;

/**
 * A map view model -- a map model, a viewport, and some event streams.
 *
 * @param <S> the type of the schema in the map model
 * @param <F> the type of the features in the map model
 */

public abstract class MapViewModel<S, F>
{
  private final EventSource<Change<Boolean>> layerVisibilityEvent = new EventSource<>();

  private final EventSource<Change<ReferencedEnvelope>> viewPortBoundsEvent = new EventSource<>();

  private final MapModel<S, F> map;

  private final MapViewport viewPort;

  private final IFeatureAdapter<F> featureAdapter;

  private final IRenderablePolygonProvider renderablePolygonProvider;

  private final ISVGProvider svgProvider;

  private final ITextSizeProvider textSizeProvider;

  protected MapViewModel(
    MapModel<S, F> map, MapViewport viewPort, IFeatureAdapter<F> featureAdapter,
    IRenderablePolygonProvider renderablePolygonProvider, ISVGProvider svgProvider,
    ITextSizeProvider textSizeProvider)
  {
    this.map = map;
    this.viewPort = viewPort;
    this.featureAdapter = featureAdapter;
    this.renderablePolygonProvider = renderablePolygonProvider;
    this.svgProvider = svgProvider;
    this.textSizeProvider = textSizeProvider;

    for(var layer : map.layers())
    {
      layer.layerVisibilityEvent().feedTo(layerVisibilityEvent);
    }

    //  TODO -- should also have some way of subscribing to add/remove of layers
  }

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

  public EventStream<Change<Boolean>> layerVisibilityEvent()
  {
    return layerVisibilityEvent;
  }

  public EventStream<Change<ReferencedEnvelope>> viewPortBoundsEvent()
  {
    return viewPortBoundsEvent;
  }

  //

  public Collection<MapLayer<S, F>> layers()
  {
    return map.layers();
  }

  public int totalRuleCount()
  {
    return map.totalRuleCount();
  }

  public MapLayer<S, F> layer(String type)
  {
    return map.layer(type);
  }

  public ReferencedEnvelope bounds()
  {
    return map.bounds();
  }

  public CoordinateReferenceSystem crs()
  {
    return bounds().getCoordinateReferenceSystem();
  }

  public abstract double adjustedDisplayScale();

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
}