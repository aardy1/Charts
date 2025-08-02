/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.platform;

import javafx.stage.Stage;

/**
 * @author graham
 */
public class MacOSX extends DesktopPlatform implements IUnderlyingPlatform {

    @Override
    public void setWindowIcons(Stage stage, Class<?> cls) {
        // Mac apps do not have window icons
    }

    public boolean isMac() {
        return true;
    }
}