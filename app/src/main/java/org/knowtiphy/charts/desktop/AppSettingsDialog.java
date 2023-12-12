package org.knowtiphy.charts.desktop;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.glyphfont.Glyph;
import org.knowtiphy.charts.Fonts;
import org.knowtiphy.charts.settings.AppSettings;
import org.knowtiphy.charts.settings.DepthUnit;
import org.knowtiphy.charts.settings.DistanceUnit;
import org.knowtiphy.charts.settings.LatLongFormat;
import org.knowtiphy.charts.settings.SpeedUnit;
import org.knowtiphy.charts.settings.TemperatureUnit;

import static org.knowtiphy.charts.utils.FXUtils.alwaysGrow;
import static org.knowtiphy.charts.utils.FXUtils.neverGrow;
import static org.knowtiphy.charts.utils.FXUtils.resizeable;

public class AppSettingsDialog
{
  //  can't set the size in CSS :-(
  private static final int BUTTON_SIZE = 16;

  public static Stage create(Window parent, int width, int height, AppSettings settings)
  {
    var unitSettings = unitSettings(settings);
    var aisSettings = aisSettings(settings);

    var content = content(unitSettings);

    var units = button("Units", Fonts.units(BUTTON_SIZE), x -> content.setCenter(unitSettings));
    var ais = button("AIS", Fonts.boat(BUTTON_SIZE), x -> content.setCenter(aisSettings));
    var buttons = buttonBar(units, ais);

    var root = new VBox();
    VBox.setVgrow(buttons, Priority.NEVER);
    VBox.setVgrow(content, Priority.ALWAYS);
    root.getChildren().addAll(buttons, content);

    var scene = new Scene(root, width, height);
    scene.getStylesheets()
         .add(AppSettingsDialog.class.getResource("settings.css").toExternalForm());

    var stage = new Stage();
    stage.setScene(scene);
    stage.sizeToScene();
    stage.initOwner(parent);
    //  TODO -- resize the stage to the content size

    return stage;
  }

  private static BorderPane content(Node initialContent)
  {
    var content = new BorderPane();
    content.getStyleClass().add("content");
    content.setCenter(initialContent);
    return content;
  }

  private static Node buttonBar(Button... buttons)
  {
    var bar = new GridPane();
    bar.getStyleClass().add("buttonbar");
    bar.addRow(0, new Region(), new ToolBar(buttons), new Region());
    bar.getColumnConstraints().addAll(alwaysGrow(), neverGrow(), alwaysGrow());
    return bar;
  }

  private static Button button(String text, Glyph glyph, EventHandler<ActionEvent> handler)
  {
    var button = new Button(text);
    button.setGraphic(glyph);
    button.setOnAction(handler);
    return button;
  }

  private static Pane unitSettings(AppSettings settings)
  {
    var pane = new GridPane();

    unitRow(pane, 0, "Distance", DistanceUnit.values(), settings.unitProfile().distanceUnit,
      settings.unitProfile().distanceUnitDecimals);
    unitRow(pane, 1, "Speed", SpeedUnit.values(), settings.unitProfile().speedUnit,
      settings.unitProfile().speedUnitDecimals);
    unitRow(pane, 2, "Depth", DepthUnit.values(), settings.unitProfile().depthUnit,
      settings.unitProfile().depthUnitDecimals);
    unitRow(pane, 3, "Temperature", TemperatureUnit.values(),
      settings.unitProfile().temperatureUnit, settings.unitProfile().temperatureUnitDecimals);

    var latLong = new Label("Lat/Long");
    var latLongCombo = resizeable(
      comboBox(LatLongFormat.values(), settings.unitProfile().latLongFormat));
    //  add a little but of extra spacing between units and Lat/Long
    GridPane.setMargin(latLong, new Insets(6, 0, 0, 0));
    GridPane.setMargin(latLongCombo, new Insets(6, 0, 0, 0));
    pane.addRow(4, latLong, latLongCombo);

    pane.getColumnConstraints().addAll(neverGrow(), alwaysGrow(), neverGrow(), alwaysGrow());

    return pane;
  }

  private static Pane aisSettings(AppSettings settings)
  {
    var pane = new GridPane();
//    pane.addRow(0, new Label("COG Predictor Length (min)"),
//      decimalDigits(settings.unitProfile().distanceUnitDecimals));
    pane.getColumnConstraints().addAll(neverGrow(), alwaysGrow());
    return pane;
  }

  private static <T> void unitRow(
    GridPane pane, int row, String labelText, T[] possibleValues, ObjectProperty<T> property,
    IntegerProperty decimals)
  {
    var editor = resizeable(comboBox(possibleValues, property));
    var decSpinner = resizeable(decimalDigits(decimals));
    pane.addRow(row, new Label(labelText), editor, new Label("Decimal Digits"), decSpinner);
  }

  private static <T> ComboBox<T> comboBox(T[] possibleValues, ObjectProperty<T> property)
  {
    var comboBox = new ComboBox<>(all(possibleValues));
    comboBox.setValue(property.getValue());
    property.bind(comboBox.valueProperty());
    return comboBox;
  }

  private static Spinner<Integer> decimalDigits(IntegerProperty property)
  {
    var spinner = new Spinner<Integer>(0, 6, property.get(), 1);
    spinner.setEditable(false);
    property.bind(spinner.valueProperty());
    return spinner;
  }

  private static <T> SimpleListProperty<T> all(T[] possibleValues)
  {
    return new SimpleListProperty<>(FXCollections.observableArrayList(possibleValues));
  }
}