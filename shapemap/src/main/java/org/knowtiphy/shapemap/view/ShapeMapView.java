package org.knowtiphy.shapemap.view;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import org.knowtiphy.shapemap.model.MapViewModel;
import org.knowtiphy.shapemap.view.canvas.CanvasShapeMapSkin;

import java.util.List;

/**
 * A shape map view -- a control that shows an ESRI shape map of layers of features of
 * some schema type.
 *
 * @param <S> the type of the schema
 * @param <F> the type of the features
 */

public class ShapeMapView<S, F> extends Control
{
  public enum SkinType
  {

    CANVAS

  }

  // CSS styling
  private static final StyleablePropertyFactory<ShapeMapView<?, ?>> FACTORY =
    new StyleablePropertyFactory<>(
    Control.getClassCssMetaData());

  private static String DEFAULT_STYLE_SHEET;

  private final SkinType skinType;

  private final MapViewModel<S, F> map;

  private final Color background;

  public ShapeMapView(MapViewModel<S, F> map, Color background)
  {
    this(map, background, SkinType.CANVAS);
  }

  public ShapeMapView(MapViewModel<S, F> map, Color background, SkinType skinType)
  {
    this.map = map;
    this.background = background;
    this.skinType = skinType;
    getStyleClass().add("shapemap-view");
  }

  @Override
  protected Skin<ShapeMapView<S, F>> createDefaultSkin()
  {
    return switch(skinType)
    {
      default -> new CanvasShapeMapSkin<>(this, map, background);
    };
  }

  @Override
  public synchronized String getUserAgentStylesheet()
  {

    switch(skinType)
    {
      case CANVAS:
      default:
        if(DEFAULT_STYLE_SHEET == null)
        {
          DEFAULT_STYLE_SHEET = ShapeMapView.class.getResource("canvas.css").toExternalForm();
        }
        return DEFAULT_STYLE_SHEET;
    }
  }

  public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData()
  {
    return FACTORY.getCssMetaData();
  }

  @Override
  public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData()
  {
    return FACTORY.getCssMetaData();
  }

  public void setMap(MapViewModel<S, F> newMap)
  {
    ((ShapeMapBaseSkin<S, F>) getSkin()).setMap(newMap);
  }
}