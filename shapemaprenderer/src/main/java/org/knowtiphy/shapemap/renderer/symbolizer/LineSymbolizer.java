/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.knowtiphy.shapemap.renderer.RenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.Stroke;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;

/**
 * @author graham
 */
public class LineSymbolizer<F> implements ISymbolizer<F> {

    private final StrokeInfo strokeInfo;

    public LineSymbolizer(StrokeInfo strokeInfo) {
        this.strokeInfo = strokeInfo;
    }

    @Override
    public void render(RenderingContext<F> context, F feature) {
        Stroke.setup(context, strokeInfo);
        Stroke.stroke(context, feature);
    }
}