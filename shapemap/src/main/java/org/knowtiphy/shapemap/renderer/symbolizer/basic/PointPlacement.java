/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.basic;

/**
 * @author graham
 */
public record PointPlacement(AnchorPoint anchorPoint, Displacement displacement) {

	public double getDisplacementX() {
		return displacement == null ? 0 : displacement.displacementX();
	}

	public double getDisplacementY() {
		return displacement == null ? 0 : displacement.displacementY();
	}

}
