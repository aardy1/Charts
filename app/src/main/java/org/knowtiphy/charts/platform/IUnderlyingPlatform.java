/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.platform;

import java.nio.file.Path;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;

/** Abstraction of the underlying platform. */
public interface IUnderlyingPlatform {

    void setStageTitle(Stage stage, String title);

    void setWindowIcons(Stage stage, Class<?> cls);

    Path rootDir();

    Path catalogsDir();

    Path chartsDir();

    Rectangle2D screenDimensions();

    double windowWidthCM(double width);

    double windowHeightCM(double width);

    double ppi();

    double ppcm();

    boolean isMac();

    void info();
}