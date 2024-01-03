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

import java.util.List;

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

  private final List<MapModel<S, F>> maps;

  private final MapViewport viewPort;

  private final IFeatureAdapter<F> featureAdapter;

  private final IRenderablePolygonProvider renderablePolygonProvider;

  private final ISVGProvider svgProvider;

  private final ITextSizeProvider textSizeProvider;

  protected MapViewModel(
    List<MapModel<S, F>> maps, MapViewport viewPort, IFeatureAdapter<F> featureAdapter,
    IRenderablePolygonProvider renderablePolygonProvider, ISVGProvider svgProvider,
    ITextSizeProvider textSizeProvider)
  {
    this.maps = maps;
    this.viewPort = viewPort;
    this.featureAdapter = featureAdapter;
    this.renderablePolygonProvider = renderablePolygonProvider;
    this.svgProvider = svgProvider;
    this.textSizeProvider = textSizeProvider;

    for(MapModel<S, F> map : maps)
    {
      for(var layer : map.layers())
      {
        layer.layerVisibilityEvent().feedTo(layerVisibilityEvent);
      }
    }

    //  TODO -- should also have some way of subscribing to add/remove of layers
  }

  public abstract double adjustedDisplayScale();

  public List<MapModel<S, F>> maps()
  {
    return maps;
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

  //  TODO -- all maps have the same bounds -- os this too strong an assumption?
  public ReferencedEnvelope bounds()
  {
    return maps.get(0).bounds();
  }

  //  TODO -- all maps have the same CRS -- os this too strong an assumption?
  public CoordinateReferenceSystem crs()
  {
    return bounds().getCoordinateReferenceSystem();
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
}