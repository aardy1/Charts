/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.shapemap.api;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * A function that computes the JavaFX bounds (bounding box) for a piece of text in a given font.
 */
public interface ITextBoundsFunction {
    Bounds getSize(Font font, String text);

    boolean overlaps(ReferencedEnvelope bounds);

    void insert(ReferencedEnvelope b1, ReferencedEnvelope b2);
}
