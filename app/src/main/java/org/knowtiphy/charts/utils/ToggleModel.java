/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.utils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * @author graham
 */
public class ToggleModel {

	private final BooleanProperty stateProperty = new SimpleBooleanProperty(false);

	public boolean isOn() {
		return stateProperty.get();
	}

	public void toggle() {
		stateProperty.set(!stateProperty.get());
	}

	public BooleanProperty getStateProperty() {
		return stateProperty;
	}

}
