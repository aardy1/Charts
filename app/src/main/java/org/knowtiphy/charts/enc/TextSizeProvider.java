/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import org.knowtiphy.charts.Fonts;
import org.knowtiphy.shapemap.api.ITextSizeProvider;

/**
 * @author graham
 */
public class TextSizeProvider implements ITextSizeProvider {
    public static final TextSizeProvider PROVIDER = new TextSizeProvider();

    private TextSizeProvider() {}

    @Override
    public Bounds apply(Font font, String s) {
        return Fonts.textSizeFast(font, s);
    }
}