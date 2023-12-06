/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import javafx.scene.paint.Color;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;

/**
 * A builder of fill information used in styling.
 *
 * The default fill color is white, and fill opacity is 1.
 */
public class FillInfoBuilder {

	private Color fill = Color.WHITE;

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
