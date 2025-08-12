/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import org.knowtiphy.shapemap.api.IFeatureFunction;
import org.knowtiphy.shapemap.renderer.RenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.DrawPoint;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.IMarkSymbolizer;

/**
 * @author graham
 */
public class PointSymbolizer<F> implements ISymbolizer<F> {

    private final IMarkSymbolizer<F> markSymbolizer;

    private final IFeatureFunction<F, Number> size;

    private final IFeatureFunction<F, Number> rotation;

    private final double opacity;

    public PointSymbolizer(
            IMarkSymbolizer<F> markSymbolizer,
            IFeatureFunction<F, Number> size,
            double opacity,
            IFeatureFunction<F, Number> rotation) {

        this.markSymbolizer = markSymbolizer;
        this.size = size;
        this.opacity = opacity;
        this.rotation = rotation;
    }

    public IFeatureFunction<F, Number> size() {
        return size;
    }

    public IFeatureFunction<F, Number> rotation() {
        return rotation;
    }

    @Override
    public void render(RenderingContext<F> context, F feature) {

        DrawPoint.setup(context, opacity);
        var geom = context.featureAdapter().defaultGeometry(feature);
        for (var i = 0; i < geom.getNumGeometries(); i++) {
            markSymbolizer.render(
                    context, feature, DrawPoint.choosePoint(context, geom.getGeometryN(i)), this);
        }
    }
}