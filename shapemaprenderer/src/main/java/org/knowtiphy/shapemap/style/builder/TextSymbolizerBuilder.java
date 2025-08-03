/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.knowtiphy.shapemap.api.IFeatureFunction;
import org.knowtiphy.shapemap.renderer.symbolizer.TextSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.LabelPlacement;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;

/**
 * @author graham
 */
public class TextSymbolizerBuilder<S, F> {

    private IFeatureFunction<F, String> label = (f, g) -> null;

    private Font font = Font.getDefault();

    private FillInfo fillInfo; // = new
    // FillInfoBuilder().fill(Color.BLACK).opacity(1).build();

    private StrokeInfo strokeInfo;

    private LabelPlacement labelPlacement;

    public TextSymbolizerBuilder<S, F> font(Font font) {
        this.font = font;
        return this;
    }

    public TextSymbolizerBuilder<S, F> label(IFeatureFunction<F, String> label) {
        this.label = label;
        return this;
    }

    public TextSymbolizerBuilder<S, F> labelPlacement(LabelPlacement labelPlacement) {
        this.labelPlacement = labelPlacement;
        return this;
    }

    public TextSymbolizerBuilder<S, F> fillInfo(FillInfo fillInfo) {
        this.fillInfo = fillInfo;
        return this;
    }

    public TextSymbolizerBuilder<S, F> strokeInfo(StrokeInfo strokeInfo) {
        this.strokeInfo = strokeInfo;
        return this;
    }

    public TextSymbolizer<S, F> build() {
        if (strokeInfo == null && fillInfo == null) {
            fillInfo = new FillInfoBuilder().fill(Color.BLACK).opacity(1).build();
        }

        return new TextSymbolizer<>(label, font, fillInfo, strokeInfo, labelPlacement);
    }
}
