/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.mark;

import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.PointSymbolizer;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 */
public interface IMarkSymbolizer<F> {

    void render(
            GraphicsRenderingContext<F> context,
            F feature,
            Point pt,
            PointSymbolizer<F> pointSymbolizer);
}
