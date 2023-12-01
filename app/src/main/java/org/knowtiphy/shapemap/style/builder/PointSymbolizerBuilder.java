/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.PointSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.IFeatureFunction;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.IMarkSymbolizer;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;

import static org.knowtiphy.shapemap.style.parser.StyleSyntaxException.expect;

/**
 * @author graham
 */
public class PointSymbolizerBuilder {

	// TODO -- spec says the default is the "native symbol size" ...?
	private static final IFeatureFunction DEFAULT_SIZE = (f, g) -> 8;

	private IMarkSymbolizer markSymbolizer;

	private IFeatureFunction<Number> size = DEFAULT_SIZE;

	private double opacity = 1;

	public PointSymbolizerBuilder markSymbolizer(IMarkSymbolizer markSymbolizer) {
		this.markSymbolizer = markSymbolizer;
		return this;
	}

	public PointSymbolizerBuilder size(IFeatureFunction<Number> size) {
		this.size = size;
		return this;
	}

	public PointSymbolizerBuilder opacity(double opacity) {
		this.opacity = opacity;
		return this;
	}

	public ISymbolizer build() throws StyleSyntaxException {

		expect(markSymbolizer, "Expected a mark symbolizer");
		return new PointSymbolizer(markSymbolizer, size, opacity);
	}

}
