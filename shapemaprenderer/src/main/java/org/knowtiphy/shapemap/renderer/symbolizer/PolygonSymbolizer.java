/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.Fill;
import org.knowtiphy.shapemap.renderer.graphics.Stroke;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;

/**
 * @author graham
 */
public class PolygonSymbolizer<F> implements ISymbolizer<F> {

    private final FillInfo fillInfo;

    private final StrokeInfo strokeInfo;

    public PolygonSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
        this.fillInfo = fillInfo;
        this.strokeInfo = strokeInfo;
    }

    @Override
    public void render(GraphicsRenderingContext<F> context, F feature) {

        var featureAdapter = context.featureAdapter();
        if (fillInfo != null) {
            Fill.setup(context, fillInfo);
            Fill.fill(
                    context,
                    featureAdapter.defaultGeometry(feature),
                    context.featureAdapter().geomType(feature));
        }

        if (strokeInfo != null) {
            Stroke.setup(context, strokeInfo);
            Stroke.stroke(
                    context,
                    featureAdapter.defaultGeometry(feature),
                    context.featureAdapter().geomType(feature));
        }
    }
}