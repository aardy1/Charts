/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.platform;

import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Region;
import javafx.stage.Screen;

/**
 * @author graham
 */
public abstract class BasePlatform {

	private final Path root;

	BasePlatform(Path root) {
		this.root = root;
	}

	public Path rootDir() {
		return root;
	}

	public Path chartsDir() {
		return rootDir().resolve(Paths.get("ENC", "US_REGION08"));
	}

	public double ppi() {
		return Screen.getPrimary().getDpi();
	}

	public double ppcm() {
		return ppi() * 2.54;
	}

	public Rectangle2D screenDimensions() {
		return Screen.getPrimary().getVisualBounds();
	}

	public double screenWidthCM() {
		var screen = screenDimensions();
		return screen.getWidth() / ppi() * 2.54;
	}

	public double windowWidthCM(Region region) {
		return screenWidthCM() * (region.getWidth() / screenDimensions().getWidth());
	}

}
