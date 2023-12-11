package org.knowtiphy.charts.utils;

import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;

/**
 * Collection of utility classes for Fx.
 */
public class FXUtils
{

  public static void later(Runnable r)
  {
    Platform.runLater(r);
  }

  public static void setDockIcon(Stage stage, InputStream stream)
  {
    stage.getIcons().add(new Image(stream));
  }

  public static <T extends Region> T resizeable(T region)
  {
    region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    return region;
  }

  public static <T extends Region> T nonResizeable(T region)
  {
    region.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    return region;
  }

  // this is still a bit hacky
  public static Tooltip tooltip(String text, Font font, int fontWidth, int width)
  {
    var prefWidth = Math.min((text.length() + 2) * fontWidth, width);
    var tooltip = new Tooltip(text);
    tooltip.setFont(font);
    tooltip.setShowDelay(Duration.millis(3));
    tooltip.setPrefWidth(prefWidth);
    tooltip.setWrapText(true);
    return tooltip;
  }

  public static ColumnConstraints gridNeverGrow()
  {
    var constraint = new ColumnConstraints();
    constraint.setHgrow(Priority.NEVER);
    return constraint;
  }

  public static ColumnConstraints gridAlwaysGrow()
  {
    var constraint = new ColumnConstraints();
    constraint.setHgrow(Priority.ALWAYS);
    constraint.setFillWidth(true);
    return constraint;
  }

}