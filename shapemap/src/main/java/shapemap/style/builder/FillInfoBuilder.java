/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.builder;

import javafx.scene.paint.Color;
import shapemap.renderer.symbolizer.basic.FillInfo;

/**
 * @author graham
 */
public class FillInfoBuilder {

	private Color fill = Color.web("#808080");

	private double opacity = 1;

	public FillInfoBuilder fill(Color fill) {
		this.fill = fill;
		return this;
	}

	public FillInfoBuilder opacity(double opacity) {
		this.opacity = opacity;
		return this;
	}

	public FillInfo build() {

		return new FillInfo(fill, opacity);
	}

}
