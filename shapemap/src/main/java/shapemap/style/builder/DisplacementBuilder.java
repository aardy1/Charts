/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.builder;

import shapemap.renderer.symbolizer.basic.Displacement;

/**
 * @author graham
 */
public class DisplacementBuilder {

	private double displacementX = 0;

	private double displacementY = 0;

	public DisplacementBuilder displacementX(double displacementX) {
		this.displacementX = displacementX;
		return this;
	}

	public DisplacementBuilder displacementY(double displacementY) {
		this.displacementY = displacementY;
		return this;
	}

	public Displacement build() {
		return new Displacement(displacementX, displacementY);
	}

}
