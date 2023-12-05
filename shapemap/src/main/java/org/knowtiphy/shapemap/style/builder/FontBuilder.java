/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.builder;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * @author graham
 */
public class FontBuilder {

	private static final Font DEFAULT_FONT = Font.getDefault();

	private String family = DEFAULT_FONT.getFamily();

	private double size = DEFAULT_FONT.getSize();

	private FontWeight weight = FontWeight.NORMAL;

	private FontPosture posture = FontPosture.REGULAR;

	public FontBuilder family(String family) {
		this.family = family;
		return this;
	}

	public FontBuilder size(double size) {
		this.size = size;
		return this;
	}

	public FontBuilder weight(FontWeight weight) {
		this.weight = weight;
		return this;
	}

	public FontBuilder posture(FontPosture posture) {
		this.posture = posture;
		return this;
	}

	public Font build() {
		return Font.font(family, weight, posture, size);
	}

}
