package org.knowtiphy.charts.chartview;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.dialog.ProgressDialog;
import org.controlsfx.glyphfont.Glyph;
import org.knowtiphy.charts.Fonts;
import org.knowtiphy.charts.enc.ChartDownloaderNotifier;
import org.knowtiphy.charts.enc.ChartLocker;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.shapemap.renderer.context.SVGCache;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;
import static org.knowtiphy.charts.chartview.AvailableCatalogs.BUILTIN_CATALOGS;
import static org.knowtiphy.charts.utils.FXUtils.alwaysGrow;
import static org.knowtiphy.charts.utils.FXUtils.neverGrow;

public class ChartLockerDialog {
    //  can't set the size in CSS :-(
    private static final int BUTTON_SIZE = 16;

    private final Stage parent;

    private final ChartLocker chartLocker;

    private final MapDisplayOptions mapDisplayOptions;

    private final SVGCache svgCache;

    public ChartLockerDialog(
            Stage parent,
            ChartLocker chartLocker,
            MapDisplayOptions mapDisplayOptions,
            SVGCache svgCache) {
        this.parent = parent;
        this.chartLocker = chartLocker;
        this.mapDisplayOptions = mapDisplayOptions;
        this.svgCache = svgCache;
    }

    public Stage create(int width, int height) {
        var stage = new Stage();

        var availableCharts = availableCharts();
        var content = content(availableCharts);

        var loadedB =
                button(
                        "Charts",
                        Fonts.boat(BUTTON_SIZE),
                        x -> content.setCenter(availableCharts()));
        var catalogsB =
                button(
                        "Catalogs",
                        Fonts.units(BUTTON_SIZE),
                        x -> content.setCenter(availableCatalogs()));

        var buttons = buttonBar(loadedB, catalogsB);

        var root = new VBox();
        root.getStyleClass().add("content");
        VBox.setVgrow(buttons, Priority.NEVER);
        VBox.setVgrow(content, Priority.ALWAYS);
        root.getChildren().addAll(buttons, content);

        var scene = new Scene(root, width, height);
        scene.getStylesheets()
                .add(ResourceLoader.class.getResource("chartlockerdialog.css").toExternalForm());

        stage.setScene(scene);
        stage.sizeToScene();
        stage.initOwner(parent);
        //  TODO -- resize the stage to the content size

        return stage;
    }

    private BorderPane content(Node initialContent) {
        var content = new BorderPane();
        content.getStyleClass().add("content");
        content.setCenter(initialContent);
        return content;
    }

    private Node buttonBar(Button... buttons) {
        var bar = new GridPane();
        bar.getStyleClass().add("buttonbar");
        bar.addRow(0, new Region(), new ToolBar(buttons), new Region());
        bar.getColumnConstraints().addAll(alwaysGrow(), neverGrow(), alwaysGrow());
        return bar;
    }

    private Button button(String text, Glyph glyph, EventHandler<ActionEvent> handler) {
        var button = new Button(text);
        button.setGraphic(glyph);
        button.setOnAction(handler);
        return button;
    }

    private ScrollPane availableCatalogs() {
        var pane = new GridPane();
        var loaded = new Label("Loaded");
        loaded.getStyleClass().add("catalogName");
        pane.addRow(0, loaded);

        var row = 1;

        for (var catalog : chartLocker.availableCatalogs()) {
            var catalogName = new Label(catalog.title());
            pane.add(catalogName, 1, row);
            row++;
        }

        var available = new Label("Available");
        available.getStyleClass().add("availableName");
        pane.addRow(row, available);
        row++;

        for (var catalog : BUILTIN_CATALOGS.entrySet()) {
            var catalogName = new Label(catalog.getKey());
            var loadButton = new Button("Load");
            loadButton.setOnAction(event -> loadCatalog(catalog.getValue()));
            pane.add(catalogName, 1, row);
            pane.add(loadButton, 2, row);
            row++;
        }

        pane.getColumnConstraints().addAll(alwaysGrow(), alwaysGrow(), alwaysGrow());

        return new ScrollPane(pane);
    }

    private ScrollPane availableCharts() {
        var catalogPanes = new VBox();

        for (var catalog : chartLocker.availableCatalogs()) {
            var grid = new GridPane();
            grid.getColumnConstraints().addAll(neverGrow(), neverGrow(), neverGrow());

            var row = 0;
            for (var cell : catalog.activeCells()) {
                var name = new Label(cell.lName());
                var scale = new Label(" 1:" + cell.cScale());
                var show = showButton(cell);
                show.setDisable(!cell.isLoaded());
                var load = loadButton(cell, show);
                load.setDisable(cell.isLoaded());
                grid.addRow(row++, name, scale, show, load);
            }

            var section = new TitledPane(catalog.title(), grid);
            section.animatedProperty().set(false);
            catalogPanes.getChildren().add(section);
        }

        var scrollPane = new ScrollPane(catalogPanes);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        return scrollPane;
    }

    private Button loadButton(ENCCell cell, Button show) {
        var button = new Button("Load");
        button.setOnAction(
                event -> {
                    var notifier = new Notifier();
                    var task = loadTask(cell, notifier);
                    var progressDialog = progressDialog(task, notifier);
                    progressDialog.showAndWait();
                    button.setDisable(true);
                    show.setDisable(false);
                });

        return button;
    }

    private Button showButton(ENCCell cell) {
        var button = new Button("Show");
        //    button.setOnAction(event -> {
        //      try
        //      {
        //        var newChart = chartLocker.loadChart(cell, mapDisplayOptions, svgCache);
        //      }
        //      catch(Exception ex)
        //      {
        //        Logger.getLogger(ChartLockerDialog.class.getName()).log(Level.SEVERE, null, ex);
        //      }
        //    });

        return button;
    }

    private void loadCatalog(URL catalogURL) {
        try {
            chartLocker.addCatalog(catalogURL);
        } catch (Exception ex) {
            Logger.getLogger(ChartLockerDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Task<Boolean> loadTask(ENCCell cell, ChartDownloaderNotifier notifier) {
        return new Task<>() {
            @Override
            protected Boolean call() {
                try {
                    chartLocker.downloadChart(cell, notifier);
                } catch (IOException interrupted) {
                    //  do nothing
                }

                return true;
            }
        };
    }

    private ProgressDialog progressDialog(Task<?> task, Notifier notifier) {
        var progress = new ProgressDialog(task);
        progress.initModality(Modality.APPLICATION_MODAL);
        progress.initStyle(StageStyle.UTILITY);
        progress.setTitle("Loading");
        progress.headerTextProperty().bind(task.titleProperty());
        progress.contentTextProperty().bind(notifier.message);
        DialogPane dialogPane = progress.getDialogPane();
        // override autosizing which fails for some reason
        dialogPane.setPrefSize(400, 200);

        progress.setOnShown(evt -> new Thread(task).start());
        task.setOnCancelled(cancelled -> progress.close());
        task.setOnSucceeded(succeeded -> progress.close());
        task.setOnFailed(failed -> progress.close());

        return progress;
    }

    private static class Notifier extends ChartDownloaderNotifier {
        public final StringProperty message = new SimpleStringProperty();

        @Override
        public void start() {
            runLater(() -> message.set("Starting Download"));
        }

        @Override
        public void reading(ENCCell cell) {
            runLater(() -> message.set("Reading cell " + cell.name()));
        }

        @Override
        public void converting(ENCCell cell) {
            runLater(() -> message.set("Preparing cell " + cell.name()));
        }

        @Override
        public void cleaningUp() {
            runLater(() -> message.set("Cleaning Up"));
        }

        @Override
        public void finished() {
            runLater(() -> message.set("Finished Download"));
        }
    }
}
// var loaded = new CheckBox("")
// {
//  @Override
//  public void arm()
//  {
//    // intentionally do nothing
//  }
// };
//        loaded.selectedProperty().set(true);