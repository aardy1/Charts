/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.charts.geotools.Coordinates;
import org.knowtiphy.charts.platform.IPlatform;
import org.knowtiphy.charts.utils.FXUtils;
import org.knowtiphy.charts.utils.ToggleModel;
import org.knowtiphy.shapemap.renderer.ShapeMapRenderer;
import org.knowtiphy.shapemap.viewmodel.MapDisplayOptions;
import org.reactfx.Subscription;

import static org.knowtiphy.charts.geotools.Coordinates.distanceAcross;

/**
 * @author graham
 */
public class InfoBar extends StackPane {

	private final Label chartName = new Label();

	private final Label chartScale = new Label();

	private final Label extentLabel = new Label();

	private final Label crsLabel = new Label();

	private final Label currentExtent = new Label();

	private final Label currentMapSpan = new Label();

	private final Label currentScreenToWorld = new Label();

	private final Label currentZoomLevel = new Label();

	private final IPlatform platform;

	private final UnitProfile unitProfile;

	private ENCChart chart;

	private final List<Subscription> subscriptions = new ArrayList<>();

	public InfoBar(IPlatform platform, ToggleModel toggleModel, ENCChart chrt, UnitProfile unitProfile,
			MapDisplayOptions displayOptions) {

		this.platform = platform;
		this.chart = chrt;
		this.unitProfile = unitProfile;

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
			}
			catch (TransformException | NonInvertibleTransformException ex) {
				Logger.getLogger(InfoBar.class.getName()).log(Level.SEVERE, null, ex);
			}
		});

		var toolbar = FXUtils.nonResizeable(new ToolBar(zoomIn, zoomOut, resetViewPort, mapDisplaySettings));
		toolbar.getStyleClass().add("controlbar");

		var buttons = new GridPane();
		buttons.addRow(0, new Region(), toolbar, new Region());
		buttons.getColumnConstraints().addAll(FXUtils.gridAlwaysGrow(), FXUtils.gridNeverGrow(),
				FXUtils.gridAlwaysGrow());

		var fixedLabels = new HBox(chartScale, crsLabel, extentLabel);
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
		subscriptions.forEach(s -> s.unsubscribe());
		subscriptions.clear();
		subscriptions.add(chart.viewPortBoundsEvent().subscribe(c -> showVariableChartInfo()));
		subscriptions.add(chart.newChartEvent().subscribe(change -> {
			chart = (ENCChart) change.getNewValue();
			showFixedChartInfo();
			showVariableChartInfo();
			setupListeners();
		}));
	}

	private void showFixedChartInfo() {
		chartName.setText(chart.title());
		chartScale.setText(ShapeMapRenderer.count.get() + "");// chart.currentScale() +
																// "");
		// chartScale.setText(chart.currentScale() + "");
		// crsLabel.setText(chart.crs().getCoordinateSystem().getName().getCode());
		// extentLabel.setText(unitProfile.envelopeLabel(chart.bounds()));
	}

	private void showVariableChartInfo() {

		var envelope = chart.viewPortBounds();
		var mapWidth = distanceAcross(envelope) / 1000;
		// currentExtent.setText(unitProfile.envelopeLabel(envelope));
		currentMapSpan
				.setText(Coordinates.twoDec(unitProfile.convertDistance(mapWidth)) + " " + unitProfile.distanceUnit);
		currentScreenToWorld.setText("1" + unitProfile.screenUnit + " : "
				+ Coordinates.twoDec(unitProfile.convertFromScreenUnit(mapWidth / platform.windowWidthCM(this)))
				+ unitProfile.distanceUnit);

		// currentZoomLevel.setText(Coordinates.twoDec(chart.getZoomFactor()) + "");
	}

}
