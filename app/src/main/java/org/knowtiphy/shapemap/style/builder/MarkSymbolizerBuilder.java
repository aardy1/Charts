/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import java.util.function.BiFunction;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.IMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.SquareMarkSymbolizer;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.style.parser.XML;

import static org.knowtiphy.shapemap.style.parser.StyleSyntaxException.expectElement;

/**
 * @author graham
 */
public class MarkSymbolizerBuilder {

	private BiFunction<FillInfo, StrokeInfo, IMarkSymbolizer> symbolizerBuilder = SquareMarkSymbolizer::new;

	private FillInfo fillInfo;

	private StrokeInfo strokeInfo;

	public MarkSymbolizerBuilder symbolizerBuilder(
			BiFunction<FillInfo, StrokeInfo, IMarkSymbolizer> symbolizerBuilder) {
		this.symbolizerBuilder = symbolizerBuilder;
		return this;
	}

	public MarkSymbolizerBuilder fillInfo(FillInfo fillInfo) {
		this.fillInfo = fillInfo;
		return this;
	}

	public MarkSymbolizerBuilder strokeInfo(StrokeInfo strokeInfo) {
		this.strokeInfo = strokeInfo;
		return this;
	}

	public IMarkSymbolizer build() throws StyleSyntaxException {
		expectElement(fillInfo, strokeInfo, XML.FILL, XML.STROKE);
		return symbolizerBuilder.apply(fillInfo, strokeInfo);
	}

}
