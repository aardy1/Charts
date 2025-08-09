/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.shapemap.api;

import javafx.scene.text.Font;

/** */
public interface ITextAdapter {

    boolean canFit(Font font, String text, double x, double y);

    //    boolean overlaps(ReferencedEnvelope bounds);
    //
    //    void insert(ReferencedEnvelope b1, ReferencedEnvelope b2);
}
