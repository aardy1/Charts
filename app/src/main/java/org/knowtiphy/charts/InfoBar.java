/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.transform.*;
import org.geotools.api.referencing.operation.*;
import org.knowtiphy.charts.chartview.*;
import org.knowtiphy.charts.enc.*;
import org.knowtiphy.charts.geotools.*;
import org.knowtiphy.charts.platform.*;
import org.knowtiphy.charts.utils.*;
import org.knowtiphy.shapemap.renderer.*;
import org.reactfx.*;

import java.util.*;
import java.util.logging.*;

import static org.knowtiphy.charts.geotools.Coordinates.*;

/**
 * @author graham
 */
public class InfoBar extends StackPane {

    private final Label chartName = new Label();

    private final Label chartScale = new Label();

    private final Label extentLabel = new Label();

    private final Label currentExtent = new Label();

    private final Label currentMapSpan = new Label();

    private final Label currentScreenToWorld = new Label();

    private final Label currentZoomLevel = new Label();

    private final IPlatform platform;

    private final UnitProfile unitProfile;

    private ENCChart chart;

    private final ChartHistory chartHistory;

    private final List<MenuItem> chartHistoryItems = new ArrayList<>();

    private final List<Subscription> subscriptions = new ArrayList<>();

    public InfoBar(
            IPlatform platform,
            ToggleModel toggleModel,
            ENCChart chrt,
            UnitProfile unitProfile,
            ChartHistory chartHistory,
            MapDisplayOptions displayOptions) {

        this.platform = platform;
        this.chart = chrt;
        this.unitProfile = unitProfile;
        this.chartHistory = chartHistory;

        var zoomIn = new Button("", Fonts.plus());
        zoomIn.setTooltip(new Tooltip("Zoom In"));
        zoomIn.setOnAction(x -> Coordinates.zoom(chart, 0.4));
        var zoomOut = new Button("", Fonts.minus());
        zoomOut.setTooltip(new Tooltip("Zoom Out"));
        zoomOut.setOnAction(x -> Coordinates.zoom(chart, 10 / 4.0));

        var mapDisplaySettings = new Button("", Fonts.setting());
        mapDisplaySettings.setTooltip(new Tooltip("Configure Map Visuals"));
        mapDisplaySettings.setOnAction(evt -> toggleModel.toggle());

        var resetViewPort = new Button("", Fonts.resetToOriginalBounds());
        resetViewPort.setTooltip(new Tooltip("Reset Map to Original Dimensions"));
        resetViewPort.setOnAction(x -> {
            try {
                chart.setViewPortBounds(chart.bounds());
            } catch (TransformException | NonInvertibleTransformException ex) {
                Logger.getLogger(InfoBar.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        // var history = new MenuButton("", Fonts.history(), chartHistoryItems);
        // history.setTooltip(new Tooltip("Chart History"));
        // history.setOnAction(x -> showHistory());

        var toolbar = FXUtils.nonResizeable(new ToolBar(
                zoomIn,
                zoomOut,
                resetViewPort,
                // history,
                mapDisplaySettings));
        toolbar.getStyleClass().add("controlbar");

        var buttons = new GridPane();
        buttons.addRow(0, new Region(), toolbar, new Region());
        buttons.getColumnConstraints()
                .addAll(FXUtils.gridAlwaysGrow(), FXUtils.gridNeverGrow(), FXUtils.gridAlwaysGrow());

        var fixedLabels = new HBox(chartScale, extentLabel);
        fixedLabels.getStyleClass().add("infobar");

        var variableLabels = new HBox(currentExtent, currentMapSpan, currentScreenToWorld, currentZoomLevel);
        variableLabels.getStyleClass().add("infobar");

        var labels = new BorderPane();
        labels.setLeft(fixedLabels);
        labels.setRight(variableLabels);

        getChildren().addAll(labels, buttons);

        showFixedChartInfo();
        showVariableChartInfo();

        widthProperty().addListener(ch -> showVariableChartInfo());
        heightProperty().addListener(ch -> showVariableChartInfo());

        setupListeners();

        ShapeMapRenderer.count.addListener(x -> showFixedChartInfo());
    }

    private void setupListeners() {
        subscriptions.forEach(Subscription::unsubscribe);
        subscriptions.clear();
        subscriptions.add(chart.viewPortBoundsEvent().subscribe(c -> showVariableChartInfo()));
        subscriptions.add(chart.newMapViewModel().subscribe(change -> {
            chart = (ENCChart) change.getNewValue();
            showFixedChartInfo();
            showVariableChartInfo();
            setupListeners();
        }));
    }

    private void showFixedChartInfo() {
        chartName.setText(chart.title());
        chartScale.setText(ShapeMapRenderer.count.get() + ""); // chart.currentScale() +
        // "");
        // chartScale.setText(chart.currentScale() + "");
        // extentLabel.setText(unitProfile.envelopeLabel(chart.bounds()));
    }

    private void showVariableChartInfo() {

        var envelope = chart.viewPortBounds();
        var mapWidth = distanceAcross(envelope) / 1000;
        // // currentExtent.setText(unitProfile.envelopeLabel(envelope));
        currentMapSpan.setText(
                Coordinates.twoDec(unitProfile.convertDistance(mapWidth)) + " " + unitProfile.distanceUnit());
        // currentScreenToWorld.setText("1" + unitProfile.screenUnit + " : "
        // + Coordinates.twoDec(unitProfile.convertFromScreenUnit(mapWidth /
        // platform.windowWidthCM(this)))
        // + unitProfile.distanceUnit);

        currentZoomLevel.setText(Coordinates.twoDec(chart.getZoomFactor()));
    }

    private ContextMenu showHistory() {

        var contextMenu = new ContextMenu();
        for (var chartDescription : chartHistory.history()) {
            var item = new MenuItem(chartDescription.getName());
            contextMenu.getItems().add(item);
        }

        return contextMenu;
    }
}