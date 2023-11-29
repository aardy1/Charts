/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer;

import javafx.scene.canvas.GraphicsContext;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.index.quadtree.Quadtree;

/**
 * @author graham
 */
//@formatter:off
public record RenderingContext(
	GraphicsContext graphicsContext,			// the canvas being drawn to
	Transformation worldToScreen,				// world to screen transformation
	double onePixelX,								// one pixel width in x direction in world coordinates
	double onePixelY,								// one pixel width in y direction in world coordinates
	RenderGeomCache renderGeomCache,	// cache for storing computed render geometries
	SVGCache svgCache,							// cache for storing computed SVG images
	Quadtree blocked,								// screen coordinates blocked from having text over them
	ReferencedEnvelope bounds //
) {}
//@formatter:on
