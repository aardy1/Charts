/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import org.knowtiphy.shapemap.renderer.symbolizer.basic.AnchorPoint;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Displacement;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.PointPlacement;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.style.parser.XML;

import static org.knowtiphy.shapemap.style.parser.StyleSyntaxException.expectElement;

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

	public PointPlacement build() throws StyleSyntaxException {
		expectElement(anchorPoint, displacement, XML.ANCHOR_POINT, XML.DISPLACEMENT);
		return new PointPlacement(anchorPoint, displacement);
	}

}
