/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.LineSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.style.parser.XML;

import static org.knowtiphy.shapemap.style.parser.StyleSyntaxException.expectElement;

/**
 * @author graham
 */
public class LineSymbolizerBuilder<S, F> {

    private StrokeInfo strokeInfo;

    public LineSymbolizerBuilder<S, F> strokeInfo(StrokeInfo strokeInfo) {
        this.strokeInfo = strokeInfo;
        return this;
    }

    public ISymbolizer<S, F> build() throws StyleSyntaxException {
        expectElement(strokeInfo, XML.STROKE);
        return new LineSymbolizer<>(strokeInfo);
    }
}
