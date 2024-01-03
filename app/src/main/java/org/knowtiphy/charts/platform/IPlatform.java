/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.platform;

import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.nio.file.Path;

/**
 * Abstraction of the underlying platform.
 */

public interface IPlatform
{
  void setStageTitle(Stage stage, String title);

  void setWindowIcons(Stage stage, Class<?> cls);
  
  Path rootDir();

  Path catalogsDir();

  Path chartsDir();

  Rectangle2D screenDimensions();

  double windowWidthCM(Region region);

  double ppi();

  double ppcm();

  boolean isMac();

  void info();

}