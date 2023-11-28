/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.platform;

import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Services;
import javafx.stage.Stage;

/**
 * @author graham
 */
public class IOS extends BasePlatform implements IPlatform {

	public IOS() {
		super(Services.get(StorageService.class).flatMap(StorageService::getPrivateStorage).get().toPath());
	}

	@Override
	public void setTitle(Stage stage, String title) {
		// iOs apps do not have window titles
	}

	@Override
	public void setWindowIcons(Stage stage, Class<?> cls) {
		// do nothing, loaded by assets
	}

}
