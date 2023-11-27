/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.dynamics;

/**
 * @author graham
 */
public class AISEvent {

	private final AISInformation aisInformation;

	public AISEvent(AISInformation aisInformation) {
		this.aisInformation = aisInformation;
	}

	public AISInformation getAisInformation() {
		return aisInformation;
	}

}
