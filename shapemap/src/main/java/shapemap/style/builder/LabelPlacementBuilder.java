/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.builder;

import shapemap.renderer.symbolizer.basic.LabelPlacement;
import shapemap.renderer.symbolizer.basic.PointPlacement;
import shapemap.style.parser.StyleSyntaxException;
import shapemap.style.parser.XML;

import static shapemap.style.parser.StyleSyntaxException.expectElement;

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
