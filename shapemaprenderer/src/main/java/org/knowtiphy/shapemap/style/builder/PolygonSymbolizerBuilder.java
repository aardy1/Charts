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
import static org.knowtiphy.shapemap.style.parser.StyleSyntaxException.expectElement;
import org.knowtiphy.shapemap.style.parser.XML;

/**
 * @author graham
 */
public class PolygonSymbolizerBuilder<F> {

    private FillInfo fillInfo;

    private StrokeInfo strokeInfo;

    public PolygonSymbolizerBuilder<F> fillInfo(FillInfo fillInfo) {
        this.fillInfo = fillInfo;
        return this;
    }

    public PolygonSymbolizerBuilder<F> strokeInfo(StrokeInfo strokeInfo) {
        this.strokeInfo = strokeInfo;
        return this;
    }

    public ISymbolizer<F> build() throws StyleSyntaxException {
        expectElement(fillInfo, strokeInfo, XML.FILL, XML.STROKE);
        return new PolygonSymbolizer<>(fillInfo, strokeInfo);
    }
}
