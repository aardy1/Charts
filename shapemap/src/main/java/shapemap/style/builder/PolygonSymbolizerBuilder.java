/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.builder;

import shapemap.renderer.symbolizer.ISymbolizer;
import shapemap.renderer.symbolizer.PolygonSymbolizer;
import shapemap.renderer.symbolizer.basic.FillInfo;
import shapemap.renderer.symbolizer.basic.StrokeInfo;
import shapemap.style.parser.StyleSyntaxException;
import shapemap.style.parser.XML;

import static shapemap.style.parser.StyleSyntaxException.expectElement;

/**
 * @author graham
 */
public class PolygonSymbolizerBuilder {

	private FillInfo fillInfo;

	private StrokeInfo strokeInfo;

	public PolygonSymbolizerBuilder fillInfo(FillInfo fillInfo) {
		this.fillInfo = fillInfo;
		return this;
	}

	public PolygonSymbolizerBuilder strokeInfo(StrokeInfo strokeInfo) {
		this.strokeInfo = strokeInfo;
		return this;
	}

	public ISymbolizer build() throws StyleSyntaxException {
		expectElement(fillInfo, strokeInfo, XML.FILL, XML.STROKE);
		return new PolygonSymbolizer(fillInfo, strokeInfo);
	}

}
