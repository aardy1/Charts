/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.platform;

import java.nio.file.Paths;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author graham
 */
public abstract class Desktop extends BasePlatform {

	Desktop() {
		super(Paths.get(System.getProperty("user.home"), "Documents", "KnowtiphyCharts"));
	}

	public void loadIcons(Stage stage, Class<?> cls) {

		stage.getIcons().addAll(//
				new Image(cls.getResourceAsStream("knowtiphy_charts_icon_32.png")),
				new Image(cls.getResourceAsStream("knowtiphy_charts_icon_64.png")));

		// Set icon on the taskbar/dock

		// if (false && Taskbar.isTaskbarSupported()) {
		// var taskbar = Taskbar.getTaskbar();
		// if (taskbar.isSupported(Feature.ICON_IMAGE)) {
		// var defaultToolkit = Toolkit.getDefaultToolkit();
		// System.err.println("ext form = " +
		// cls.getResource("knowtiphy_charts_icon_32.png"));
		// var dockIcon =
		// defaultToolkit.getImage(cls.getResource("knowtiphy_charts_icon_32.png"));
		// taskbar.setIconImage(dockIcon);
		// }
		// }
	}

}
