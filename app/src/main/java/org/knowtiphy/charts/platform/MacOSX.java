/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.platform;

import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.stage.Stage;

/**
 * @author graham
 */
public class MacOSX extends Desktop implements IPlatform {

	@Override
	public void setTitle(Stage stage, String title) {
		// Mac apps do not have window titles
	}

	@Override
	public void setWindowIcons(Stage stage, Class<?> cls) {
		// Mac apps do not have window icons
	}

	@Override
	public Path chartsDir() {
		return rootDir().resolve(Paths.get("ENC", "08_REGION"));
	}

}
