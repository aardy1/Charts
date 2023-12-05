/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.symbolizer.mark;

import shapemap.renderer.symbolizer.basic.FillInfo;
import shapemap.renderer.api.IFeature;
import shapemap.renderer.symbolizer.basic.StrokeInfo;

/**
 * @author graham
 */
public abstract class BaseMarkSymbolizer<F extends IFeature> implements IMarkSymbolizer<F> {

	protected final FillInfo fillInfo;

	protected final StrokeInfo strokeInfo;

	protected BaseMarkSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
		this.fillInfo = fillInfo;
		this.strokeInfo = strokeInfo;
	}

}
