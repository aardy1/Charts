/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.builder;

import shapemap.renderer.symbolizer.ISymbolizer;
import shapemap.renderer.symbolizer.LineSymbolizer;
import shapemap.renderer.symbolizer.basic.StrokeInfo;
import shapemap.style.parser.StyleSyntaxException;
import shapemap.style.parser.XML;

import static shapemap.style.parser.StyleSyntaxException.expectElement;

/**
 * @author graham
 */
public class LineSymbolizerBuilder {

	private StrokeInfo strokeInfo;

	public LineSymbolizerBuilder strokeInfo(StrokeInfo strokeInfo) {
		this.strokeInfo = strokeInfo;
		return this;
	}

	public ISymbolizer build() throws StyleSyntaxException {
		expectElement(strokeInfo, XML.STROKE);
		return new LineSymbolizer(strokeInfo);
	}

}
