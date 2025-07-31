/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer;

import javafx.scene.canvas.GraphicsContext;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.renderer.context.RenderingContext;
import org.locationtech.jts.index.quadtree.Quadtree;

/**
 * @author graham
 */
// @formatter:off
public record GraphicsRenderingContext<S, F>(
        RenderingContext<S, F> renderingContext,
        // the canvas being drawn to
        GraphicsContext graphicsContext,
        Transformation worldToScreen, // world to screen transformation
        double onePixelX, // one pixel width in x direction in world coordinates
        double onePixelY, // one pixel width in y direction in world coordinates
        Quadtree blocked, // screen coordinates blocked from having text over them
        ReferencedEnvelope bounds) {}
// @formatter:on
