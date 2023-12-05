/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.symbolizer;

import shapemap.renderer.GraphicsRenderingContext;

/**
 * @author graham
 */
public interface ISymbolizer<F> {

	void render(GraphicsRenderingContext context, F feature);

}
