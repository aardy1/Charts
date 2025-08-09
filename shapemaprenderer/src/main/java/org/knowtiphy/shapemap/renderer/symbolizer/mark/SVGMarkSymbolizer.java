/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.shapemap.renderer.symbolizer.mark;

import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.PointSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.PathInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 * @param <S> the type of the feature schema
 * @param <F> the type of the features conforming to the schema
 */
public class SVGMarkSymbolizer<S, F> extends BaseMarkSymbolizer<S, F> {

    private final PathInfo pathInfo;

    public SVGMarkSymbolizer(PathInfo pathInfo, FillInfo fillInfo, StrokeInfo strokeInfo) {
        super(fillInfo, strokeInfo);
        this.pathInfo = pathInfo;
    }

    @Override
    public void render(
            GraphicsRenderingContext<S, F, ?> context,
            F feature,
            Point pt,
            PointSymbolizer<S, F> pointSymbolizer) {

        var szo = pointSymbolizer.size().apply(feature, pt);
        if (szo == null) {
            return;
        }

        var rotationO =
                pointSymbolizer.rotation() == null
                        ? null
                        : pointSymbolizer.rotation().apply(feature, pt);
        var rotation = rotationO == null ? 0 : rotationO.doubleValue();
        var image =
                context.renderingContext()
                        .svgProvider()
                        .get(pathInfo.name(), szo.intValue(), rotation);

        var x = pt.getX();
        var y = pt.getY();
        var sizeX = image.getWidth() * context.onePixelX();
        var sizeY = image.getHeight() * context.onePixelY();
        var halfSizeX = sizeX / 2;
        var halfSizeY = sizeY / 2;

        // TODO -- make the image fetcher into a feature function of some sort and put the
        // provider in there
        context.graphicsContext().drawImage(image, x - halfSizeX, y - halfSizeY, sizeX, sizeY);
    }
}
