package org.knowtiphy.shapemap.view;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import org.knowtiphy.shapemap.model.Quilt;
import org.knowtiphy.shapemap.view.canvas.CanvasShapeMapSkin;

import java.util.List;
import java.util.Objects;

/**
 * A shape map view -- a control that shows an ESRI shape map of layers of
 * features of some schema type.
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

  private final Quilt<S, F> map;

  private final Color background;

  public ShapeMapView(Quilt<S, F> map, Color background)
  {
    this.map = map;
    this.background = background;
    this.skinType = SkinType.CANVAS;
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
          DEFAULT_STYLE_SHEET = Objects
                                  .requireNonNull(ShapeMapView.class.getResource("canvas.css"))
                                  .toExternalForm();
        }
        return DEFAULT_STYLE_SHEET;
    }
  }

  @Override
  public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData()
  {
    return FACTORY.getCssMetaData();
  }
}

//    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData()
//    {
//        return FACTORY.getCssMetaData();
//    }