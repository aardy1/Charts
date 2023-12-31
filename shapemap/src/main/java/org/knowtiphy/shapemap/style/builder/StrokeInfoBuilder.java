/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import javafx.scene.paint.Color;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;

/**
 * A builder of stroke information used in styling.
 * <p>
 * The default stroke color is white, stroke width is 1, and stroke opacity is 1.
 */
public class StrokeInfoBuilder
{

  private Color color = Color.BLACK;

  private int width = 1;

  private double opacity = 1;

  public StrokeInfoBuilder color(Color color)
  {
    this.color = color;
    return this;
  }

  public StrokeInfoBuilder width(int width)
  {
    this.width = width;
    return this;
  }

  public StrokeInfoBuilder opacity(double opacity)
  {
    this.opacity = opacity;
    return this;
  }

  public StrokeInfo build()
  {

    return new StrokeInfo(color, width, opacity);
  }

}