/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import org.knowtiphy.charts.Fonts;
import org.knowtiphy.charts.utils.FXUtils;
import org.knowtiphy.shapemap.api.ITextBoundsFunction;

/**
 * the default function that computes the JavaFX bounds (bounding box) for a piece of text in a
 * given font.
 */
public class DefaultTextBoundsFunction implements ITextBoundsFunction {

    public static final DefaultTextBoundsFunction FUNCTION = new DefaultTextBoundsFunction();

    private DefaultTextBoundsFunction() {}

    @Override
    public Bounds apply(Font font, String s) {
        return FXUtils.textSizeFast(font, s);
    }
}
