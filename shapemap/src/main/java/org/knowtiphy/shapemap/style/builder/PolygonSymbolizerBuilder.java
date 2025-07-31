/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.PolygonSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.style.parser.XML;

import static org.knowtiphy.shapemap.style.parser.StyleSyntaxException.expectElement;

/**
 * @author graham
 */
public class PolygonSymbolizerBuilder<S, F> {

    private FillInfo fillInfo;

    private StrokeInfo strokeInfo;

    public PolygonSymbolizerBuilder<S, F> fillInfo(FillInfo fillInfo) {
        this.fillInfo = fillInfo;
        return this;
    }

    public PolygonSymbolizerBuilder<S, F> strokeInfo(StrokeInfo strokeInfo) {
        this.strokeInfo = strokeInfo;
        return this;
    }

    public ISymbolizer<S, F> build() throws StyleSyntaxException {
        expectElement(fillInfo, strokeInfo, XML.FILL, XML.STROKE);
        return new PolygonSymbolizer<>(fillInfo, strokeInfo);
    }
}
