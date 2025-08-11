/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts;

import javafx.scene.text.Font;
import org.geotools.api.geometry.BoundingBox;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.knowtiphy.charts.utils.FXUtils;
import org.knowtiphy.shapemap.api.ITextAdapter;
import org.locationtech.jts.index.quadtree.Quadtree;

/**
 * the default function that computes the JavaFX bounds (bounding box) for a piece of text in a
 * given font.
 */
public class TextAdapter implements ITextAdapter {

    //  quadrees are good for rectangular areas
    private final Quadtree index;

    public TextAdapter() {
        index = new Quadtree();
    }

    @Override
    public boolean canFit(Font font, String s, double x, double y) {

        var fxBounds = FXUtils.textSizeFast(font, s);
        assert fxBounds.getWidth() != 0 && fxBounds.getHeight() != 0;

        var textBounds =
                new ReferencedEnvelope(
                        x,
                        x + fxBounds.getWidth(),
                        //  TODO -- need to work out which it is
                        y - fxBounds.getHeight(),
                        y + fxBounds.getHeight(),
                        DefaultEngineeringCRS.CARTESIAN_2D);

        if (!overlaps(textBounds)) {
            index.insert(textBounds, textBounds);
            return true;
        }

        return false;
    }

    private boolean overlaps(ReferencedEnvelope bounds) {
        for (var box : index.query(bounds)) {
            if (((BoundingBox) box).intersects(bounds)) {
                //                System.err.println(" Real intersection");
                return true;
            }
        }

        return false;
    }
}
