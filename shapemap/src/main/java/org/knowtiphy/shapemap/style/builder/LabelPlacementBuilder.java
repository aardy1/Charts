/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import org.knowtiphy.shapemap.renderer.symbolizer.basic.LabelPlacement;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.PointPlacement;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.style.parser.XML;

import static org.knowtiphy.shapemap.style.parser.StyleSyntaxException.expectElement;

/**
 * @author graham
 */
public class LabelPlacementBuilder {

    private PointPlacement pointPlacement;

    public LabelPlacementBuilder pointPlacement(PointPlacement pointPlacement) {
        this.pointPlacement = pointPlacement;
        return this;
    }

    public LabelPlacement build() throws StyleSyntaxException {
        expectElement(pointPlacement, XML.POINT_PLACEMENT);
        return new LabelPlacement(pointPlacement);
    }
}
