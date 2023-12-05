/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.symbolizer.basic;

/**
 * @author graham
 */
public record AnchorPoint(Double anchorX, Double anchorY) {

	public Double getAnchorX() {
		return anchorX == null ? 0 : anchorX;
	}

	public Double getAnchorY() {
		return anchorY == null ? 0 : anchorY;
	}

}
