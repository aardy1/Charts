/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.LineSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import static org.knowtiphy.shapemap.style.parser.StyleSyntaxException.expectElement;
import org.knowtiphy.shapemap.style.parser.XML;

/**
 * @author graham
 */
public class LineSymbolizerBuilder<F> {

    private StrokeInfo strokeInfo;

    public LineSymbolizerBuilder<F> strokeInfo(StrokeInfo strokeInfo) {
        this.strokeInfo = strokeInfo;
        return this;
    }

    public ISymbolizer<F> build() throws StyleSyntaxException {
        expectElement(strokeInfo, XML.STROKE);
        return new LineSymbolizer<>(strokeInfo);
    }
}
