/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartview;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.BeanProperty;
import org.reactfx.Change;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

/**
 * @author graham
 */
public class MapDisplayOptions {

	private final BooleanProperty showGridProperty = new SimpleBooleanProperty(true);

	private final BooleanProperty showLights = new SimpleBooleanProperty(false);

	private final BooleanProperty showSoundings = new SimpleBooleanProperty(false);

	private final BooleanProperty showPlatforms = new SimpleBooleanProperty(false);

	private final BooleanProperty showWrecks = new SimpleBooleanProperty(false);

	private final ObservableList<PropertySheet.Item> properties = FXCollections.observableArrayList();

	public final EventStream<Change<Boolean>> showLightsEvents = EventStreams.changesOf(showLights);

	public final EventStream<Change<Boolean>> showPlatformEvents = EventStreams.changesOf(showPlatforms);

	public final EventStream<Change<Boolean>> showSoundingsEvents = EventStreams.changesOf(showSoundings);

	public final EventStream<Change<Boolean>> showGridEvents = EventStreams.changesOf(showGridProperty);

	public final EventStream<Change<Boolean>> showWreckEvents = EventStreams.changesOf(showWrecks);

	public MapDisplayOptions() throws IntrospectionException {

		var gridDescriptor = new PropertyDescriptor("Show Grid", MapDisplayOptions.class, "getShowGrid", "setShowGrid");
		gridDescriptor.setValue(BeanProperty.CATEGORY_LABEL_KEY, "Other");
		gridDescriptor.setShortDescription("Show the lat-long grid");
		properties.add(new BeanProperty(this, gridDescriptor));

		showFeature("Lights");
		showFeature("Soundings");
		showFeature("Platforms");
		showFeature("Wrecks");
	}

	private void showFeature(String name) throws IntrospectionException {
		var descriptor = new PropertyDescriptor("Show " + name, MapDisplayOptions.class, "getShow" + name,
				"setShow" + name);
		descriptor.setValue(BeanProperty.CATEGORY_LABEL_KEY, "Show Features");
		properties.add(new BeanProperty(this, descriptor));
	}

	public void setShowGrid(boolean state) {
		showGridProperty.set(state);
	}

	public boolean getShowGrid() {
		return showGridProperty.get();
	}

	public void setShowLights(boolean state) {
		showLights.set(state);
	}

	public boolean getShowLights() {
		return showLights.get();
	}

	public boolean getShowSoundings() {
		return showSoundings.get();
	}

	public void setShowSoundings(boolean state) {
		showSoundings.set(state);
	}

	public void setShowPlatforms(boolean state) {
		showPlatforms.set(state);
	}

	public boolean getShowPlatforms() {
		return showPlatforms.get();
	}

	public void setShowWrecks(boolean state) {
		showWrecks.set(state);
	}

	public boolean getShowWrecks() {
		return showWrecks.get();
	}

	public ObservableList<PropertySheet.Item> getProperties() {
		return properties;
	}

}
