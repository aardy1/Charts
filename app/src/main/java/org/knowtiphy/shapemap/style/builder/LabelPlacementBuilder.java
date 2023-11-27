/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import org.knowtiphy.shapemap.renderer.symbolizer.basic.LabelPlacement;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.PointPlacement;
import org.knowtiphy.shapemap.style.*;

/**
 * @author graham
 */
public class LabelPlacementBuilder {

	private PointPlacement pointPlacement;

	public LabelPlacementBuilder pointPlacement(PointPlacement pointPlacement) {
		this.pointPlacement = pointPlacement;
		return this;
	}

	public LabelPlacement build() {
		return new LabelPlacement(pointPlacement);
	}

}
