/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import org.geotools.api.geometry.BoundingBox;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.utils.FXUtils;
import org.knowtiphy.shapemap.api.ITextBoundsFunction;
import org.locationtech.jts.index.quadtree.Quadtree;

/**
 * the default function that computes the JavaFX bounds (bounding box) for a piece of text in a
 * given font.
 */
public class DefaultTextBoundsFunction implements ITextBoundsFunction {

    //    public static final DefaultTextBoundsFunction FUNCTION = new DefaultTextBoundsFunction();
    private Quadtree index;

    public DefaultTextBoundsFunction() {
        index = new Quadtree();
    }

    @Override
    public Bounds getSize(Font font, String s) {
        return FXUtils.textSizeFast(font, s);
    }

    @Override
    public boolean overlaps(ReferencedEnvelope bounds) {
        for (var box : index.query(bounds)) {
            if (((BoundingBox) box).intersects(bounds)) {
                //                System.err.println(" Real intersection");
                return true;
            }
        }

        return false;
    }

    @Override
    public void insert(ReferencedEnvelope b1, ReferencedEnvelope b2) {
        index.insert(b1, b2);
    }
}
