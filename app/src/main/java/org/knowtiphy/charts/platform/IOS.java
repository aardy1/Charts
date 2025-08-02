/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.platform;

import javafx.stage.Stage;

import java.nio.file.Paths;

/**
 * @author graham
 */
public class IOS extends BaseUnderlyingPlatform implements IUnderlyingPlatform {

    public IOS() {
        super(Paths.get(System.getProperty("user.home"))); // , "Documents", "Knowtiphy
        // Charts"));
        // super(Services.get(StorageService.class).flatMap(StorageService::getPrivateStorage).get()
        // .toPath());
    }

    // public Path chartsDir() {
    // return rootDir().resolve(Paths.get("ENC", "USREGION08"));
    // }

    @Override
    public void setStageTitle(Stage stage, String title) {
        // iOs apps do not have window titles
    }

    @Override
    public void setWindowIcons(Stage stage, Class<?> cls) {
        // do nothing, loaded by assets
    }
}