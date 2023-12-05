/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.builder;

import shapemap.renderer.symbolizer.basic.AnchorPoint;
import shapemap.renderer.symbolizer.basic.Displacement;
import shapemap.renderer.symbolizer.basic.PointPlacement;
import shapemap.style.parser.StyleSyntaxException;
import shapemap.style.parser.XML;

import static shapemap.style.parser.StyleSyntaxException.expectElement;

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
