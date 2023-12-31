/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.IMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.SquareMarkSymbolizer;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;

import java.util.function.BiFunction;

/**
 * @author graham
 */
public class MarkSymbolizerBuilder<S, F>
{

  private BiFunction<FillInfo, StrokeInfo, IMarkSymbolizer<S, F>> symbolizerBuilder =
    SquareMarkSymbolizer::new;

  private FillInfo fillInfo;

  private StrokeInfo strokeInfo;

  public MarkSymbolizerBuilder<S, F> symbolizerBuilder(
    BiFunction<FillInfo, StrokeInfo, IMarkSymbolizer<S, F>> symbolizerBuilder)
  {
    this.symbolizerBuilder = symbolizerBuilder;
    return this;
  }

  public MarkSymbolizerBuilder<S, F> fillInfo(FillInfo fillInfo)
  {
    this.fillInfo = fillInfo;
    return this;
  }

  public MarkSymbolizerBuilder<S, F> strokeInfo(StrokeInfo strokeInfo)
  {
    this.strokeInfo = strokeInfo;
    return this;
  }

  public IMarkSymbolizer<S, F> build() throws StyleSyntaxException
  {
    // expectElement(fillInfo, strokeInfo, XML.FILL, XML.STROKE);
    return symbolizerBuilder.apply(fillInfo, strokeInfo);
  }

}