/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.context;

import shapemap.renderer.api.IRenderablePolygonProvider;
import shapemap.renderer.api.ISVGProvider;

/**
 * @author graham
 */
public record RendererContext(IRenderablePolygonProvider renderablePolygonProvider, ISVGProvider svgProvider) {

}
