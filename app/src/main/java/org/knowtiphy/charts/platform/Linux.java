/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.platform;

import javafx.stage.Stage;

/**
 * @author graham
 */
public class Linux extends Desktop implements IPlatform {

    @Override
    public void setStageTitle(Stage stage, String title) {
        // Mac apps do not have window titles
    }

    @Override
    public void setWindowIcons(Stage stage, Class<?> cls) {
        // Mac apps do not have window icons
    }
}