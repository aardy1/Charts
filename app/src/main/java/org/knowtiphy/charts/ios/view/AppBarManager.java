/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.ios.view;

import com.gluonhq.charm.glisten.application.AppManager;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;

/**
 * @author graham
 */
public class AppBarManager {

	public static void updateAppBar(String currentView) {

		final List<MenuItem> menu = new ArrayList<>();
		for (var view : Names.VIEW_NAMES) {
			if (!view.equals(currentView)) {
				var menuItem = new MenuItem(Names.VIEW_NAME.get(view));
				menuItem.setOnAction((ActionEvent e) -> AppManager.getInstance().switchView(view));
				menu.add(menuItem);
			}
		}

		AppManager.getInstance().getAppBar().getMenuItems().setAll(menu);
	}

}
