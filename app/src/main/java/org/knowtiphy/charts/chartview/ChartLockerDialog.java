package org.knowtiphy.charts.chartview;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.glyphfont.Glyph;
import org.knowtiphy.charts.Fonts;
import org.knowtiphy.charts.enc.ChartLocker;

import java.net.URL;
import java.util.Map;

import static org.knowtiphy.charts.chartview.AvailableCatalogs.AVAILABLE_CATALOGS;
import static org.knowtiphy.charts.utils.FXUtils.alwaysGrow;
import static org.knowtiphy.charts.utils.FXUtils.neverGrow;
import static org.knowtiphy.charts.utils.FXUtils.resizeable;

public class ChartLockerDialog
{
  //  can't set the size in CSS :-(
  private static final int BUTTON_SIZE = 16;

  public static Stage create(Window parent, int width, int height, ChartLocker chartLocker)
  {

    var loaded = loadedCharts(chartLocker);
    var catalogs = availableCatalogs(chartLocker);

    var content = content(loaded);

    var loadedB = button("Loaded Charts", Fonts.boat(BUTTON_SIZE), x -> content.setCenter(loaded));
    var catalogsB = button("Available Catalogs", Fonts.units(BUTTON_SIZE),
      x -> content.setCenter(catalogs));

    var buttons = buttonBar(loadedB, catalogsB);

    var root = new VBox();
    VBox.setVgrow(buttons, Priority.NEVER);
    VBox.setVgrow(content, Priority.ALWAYS);
    root.getChildren().addAll(buttons, content);

    var scene = new Scene(root, width, height);
    scene.getStylesheets()
         .add(ResourceLoader.class.getResource("chartlocker.css").toExternalForm());

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

  private static ScrollPane availableCatalogs(ChartLocker chartLocker)
  {
    var pane = new GridPane();
    var loaded = new Label("Loaded");
    loaded.getStyleClass().add("catalogName");
    pane.addRow(0, loaded);

    var row = 1;

    for(var catalog : chartLocker.chartLoader().availableCatalogs())
    {
      var catalogName = new Label(catalog.getTitle());
      pane.add(catalogName, 1, row);
      row++;
    }

    var available = new Label("Available");
    available.getStyleClass().add("availableName");
    pane.addRow(row, available);
    row++;

    for(var catalog : AVAILABLE_CATALOGS.entrySet())
    {
      var catalogName = new Label(catalog.getKey());
      var loadButton = new Button("Load");
      loadButton.setOnAction(event -> loadCatalog(chartLocker, catalog));
      pane.add(catalogName, 1, row);
      pane.add(loadButton, 2, row);
      row++;
    }

    pane.getColumnConstraints().addAll(alwaysGrow(), alwaysGrow(), alwaysGrow());

    return new ScrollPane(pane);
  }

  private static ScrollPane loadedCharts(ChartLocker chartLocker)
  {
    var pane = new GridPane();
    var row = 0;
    for(var chart : chartLocker.chartLoader().getChartDescriptions())
    {
      var chartName = new Label(chart.getName());
      var chartScale = new Label(" 1:" + chart.cScale());
      var loaded = new CheckBox("")
      {
        @Override
        public void arm()
        {
          // intentionally do nothing
        }
      };
      loaded.selectedProperty().set(true);
      pane.addRow(row, chartName, chartScale);
      row++;
    }

    pane.getColumnConstraints().addAll(alwaysGrow(), alwaysGrow());

    return new ScrollPane(pane);
  }

  private static void loadCatalog(ChartLocker chartLocker, Map.Entry<String, URL> catalogURL)
  {
    System.err.println("Load catalog " + catalogURL);
    var catalog = chartLocker.chartLoader().readCatalog(catalogURL.getValue());
    System.err.println("Catalog = " + catalog);
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