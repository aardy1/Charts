/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.mark;

import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;

/**
 * @author graham
 */
public abstract class BaseMarkSymbolizer<F> implements IMarkSymbolizer<F> {

    protected final FillInfo fillInfo;

    protected final StrokeInfo strokeInfo;

    protected BaseMarkSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
        this.fillInfo = fillInfo;
        this.strokeInfo = strokeInfo;
    }
}
