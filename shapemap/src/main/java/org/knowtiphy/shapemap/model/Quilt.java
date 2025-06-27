package org.knowtiphy.shapemap.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.api.ITextSizeProvider;
import org.reactfx.Change;

import java.util.List;

/**
 * A map view model for a collection of map views quilted together
 *
 * @param <S> the type of the schema in the map model
 * @param <F> the type of the features in the map model
 */

public class Quilt<S, F> extends BaseMapViewModel<S, F>
{
  private List<MapModel<S, F>> maps;

  private final MapViewport viewPort;

  protected Quilt(
    List<MapModel<S, F>> maps, MapViewport viewPort, IFeatureAdapter<F> featureAdapter,
    IRenderablePolygonProvider renderablePolygonProvider, ISVGProvider svgProvider,
    ITextSizeProvider textSizeProvider)
  {
    super(featureAdapter, renderablePolygonProvider, svgProvider, textSizeProvider);
    this.viewPort = viewPort;
    this.maps = maps;

    for(var map : maps)
    {
      for(var layer : map.layers())
      {
        layer.layerVisibilityEvent().feedTo(layerVisibilityEvent);
      }
    }

    //  TODO -- should also have some way of subscribing to add/remove of layers
  }

  public List<MapModel<S, F>> maps()
  {
    return maps;
  }

  public void setMaps(List<MapModel<S, F>> maps)
  {
    this.maps = maps;
  }

  public void setViewPortBounds(ReferencedEnvelope bounds)
    throws TransformException, NonInvertibleTransformException
  {
    System.err.println("Set view port bounds = " + bounds);
    var oldBounds = viewPort.bounds();
    viewPort.setBounds(bounds);
    viewPortBoundsEvent.push(new Change<>(oldBounds, bounds));
  }

  @Override
  public void setViewPortScreenArea(Rectangle2D bounds)
    throws TransformException, NonInvertibleTransformException
  {
    viewPort.setScreenArea(bounds);
  }

  @Override
  public Affine viewPortScreenToWorld()
  {
    return viewPort.screenToWorld();
  }

  @Override
  public Affine viewPortWorldToScreen()
  {
    return viewPort.worldToScreen();
  }

  @Override
  public double displayScale()
  {
    return maps().get(0).cScale() / zoom();
  }

  @Override
  public ReferencedEnvelope bounds()
  {
    return viewPort.bounds();
  }

  @Override
  public ReferencedEnvelope viewPortBounds()
  {
    return viewPort.bounds();
  }

  @Override
  public Rectangle2D viewPortScreenArea()
  {
    return viewPort.screenArea();
  }
}