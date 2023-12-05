/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.context;

import org.knowtiphy.shapemap.renderer.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.renderer.api.ISVGProvider;

/**
 * @author graham
 */
public record RendererContext(IRenderablePolygonProvider renderablePolygonProvider, ISVGProvider svgProvider) {

}
