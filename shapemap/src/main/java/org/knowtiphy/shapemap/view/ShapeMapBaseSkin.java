package org.knowtiphy.shapemap.view;

import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;

public abstract class ShapeMapBaseSkin<S, F> extends SkinBase<ShapeMapView<S, F>>
  implements Skin<ShapeMapView<S, F>>
{

  private static final double MINIMUM_WIDTH = 8;

  private static final double MINIMUM_HEIGHT = 8;

  private static final double MAXIMUM_WIDTH = Double.MAX_VALUE;

  private static final double MAXIMUM_HEIGHT = Double.MAX_VALUE;

  protected ShapeMapBaseSkin(ShapeMapView<S, F> surface)
  {
    super(surface);
  }

  @Override
  protected double computeMinWidth(
    final double height, final double top, final double right, final double bottom,
    final double left)
  {
    return MINIMUM_WIDTH;
  }

  @Override
  protected double computeMinHeight(
    final double width, final double top, final double right, final double bottom,
    final double left)
  {
    return MINIMUM_HEIGHT;
  }

  @Override
  protected double computeMaxWidth(
    final double width, final double top, final double right, final double bottom,
    final double left)
  {
    return MAXIMUM_WIDTH;
  }

  @Override
  protected double computeMaxHeight(
    final double width, final double top, final double right, final double bottom,
    final double left)
  {
    return MAXIMUM_HEIGHT;
  }

  protected ShapeMapView<S, F> S()
  {
    return getSkinnable();
  }

//  protected abstract void setMapViewModel(SingleMapViewModel<S, F> newMap);
}