/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import java.util.function.BiFunction;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.IMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.SquareMarkSymbolizer;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;

/**
 * @author graham
 */
public class MarkSymbolizerBuilder<F extends IFeature> {

	private BiFunction<FillInfo, StrokeInfo, IMarkSymbolizer<F>> symbolizerBuilder = SquareMarkSymbolizer::new;

	private FillInfo fillInfo;

	private StrokeInfo strokeInfo;

	public MarkSymbolizerBuilder<F> symbolizerBuilder(
			BiFunction<FillInfo, StrokeInfo, IMarkSymbolizer<F>> symbolizerBuilder) {
		this.symbolizerBuilder = symbolizerBuilder;
		return this;
	}

	public MarkSymbolizerBuilder<F> fillInfo(FillInfo fillInfo) {
		this.fillInfo = fillInfo;
		return this;
	}

	public MarkSymbolizerBuilder<F> strokeInfo(StrokeInfo strokeInfo) {
		this.strokeInfo = strokeInfo;
		return this;
	}

	public IMarkSymbolizer<F> build() throws StyleSyntaxException {
		// expectElement(fillInfo, strokeInfo, XML.FILL, XML.STROKE);
		return symbolizerBuilder.apply(fillInfo, strokeInfo);
	}

}
