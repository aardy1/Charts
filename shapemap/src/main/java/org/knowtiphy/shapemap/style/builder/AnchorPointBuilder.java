/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import org.knowtiphy.shapemap.renderer.symbolizer.basic.AnchorPoint;

/**
 * @author graham
 */
public class AnchorPointBuilder {

    private Double anchorX;

    private Double anchorY;

    public AnchorPointBuilder anchorX(Double anchorX) {
        this.anchorX = anchorX;
        return this;
    }

    public AnchorPointBuilder anchorY(Double anchorY) {
        this.anchorY = anchorY;
        return this;
    }

    public AnchorPoint build() {
        return new AnchorPoint(anchorX, anchorY);
    }
}
