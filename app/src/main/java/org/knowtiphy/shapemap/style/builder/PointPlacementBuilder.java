/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import org.knowtiphy.shapemap.renderer.symbolizer.basic.PointPlacement;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.AnchorPoint;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Displacement;
import org.knowtiphy.shapemap.style.*;

/**
 * @author graham
 */
public class PointPlacementBuilder {

	private AnchorPoint anchorPoint;

	private Displacement displacement;

	public PointPlacementBuilder anchorPoint(AnchorPoint anchorPoint) {
		this.anchorPoint = anchorPoint;
		return this;
	}

	public PointPlacementBuilder displacement(Displacement displacement) {
		this.displacement = displacement;
		return this;
	}

	public PointPlacement build() {
		return new PointPlacement(anchorPoint, displacement);
	}

}
