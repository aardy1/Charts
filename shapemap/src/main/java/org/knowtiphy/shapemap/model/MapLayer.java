package org.knowtiphy.shapemap.model;

import org.knowtiphy.shapemap.api.IFeatureSource;
import org.knowtiphy.shapemap.renderer.FeatureTypeStyle;

public class MapLayer<S, F>
{
  private final IFeatureSource<S, F> featureSource;

  private final FeatureTypeStyle<S, F> style;

  private boolean visible;

  private final boolean scaleLess;

  public MapLayer(
    IFeatureSource<S, F> featureSource, FeatureTypeStyle<S, F> style, boolean visible,
    boolean scaleLess)
  {
    this.featureSource = featureSource;
    this.style = style;
    this.visible = visible;
    this.scaleLess = scaleLess;
  }

  public IFeatureSource<S, F> featureSource()
  {
    return featureSource;
  }

  public FeatureTypeStyle<S, F> style()
  {
    return style;
  }

  public boolean isVisible()
  {
    return visible;
  }

  void setVisible(boolean visible)
  {
    this.visible = visible;
  }

  public boolean isScaleLess()
  {
    return scaleLess;
  }
}