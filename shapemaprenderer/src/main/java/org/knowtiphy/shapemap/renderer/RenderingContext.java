/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer;

import javafx.scene.canvas.GraphicsContext;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.api.ITextAdapter;

/**
 * @author graham
 */
public record RenderingContext<F>(
        // the canvas being drawn to
        GraphicsContext graphicsContext,
        Transformation worldToScreen, // world to screen transformation
        IFeatureAdapter<F> featureAdapter,
        IRenderablePolygonProvider<F> renderablePolygonProvider,
        ITextAdapter textSizeProvider,
        ISVGProvider svgProvider,
        double onePixelX, // one pixel width in x direction in world coordinates
        double onePixelY // , // one pixel width in y direction in world coordinates
        ) {}
