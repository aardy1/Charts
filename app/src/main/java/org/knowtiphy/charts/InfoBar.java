/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts;

import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.charts.enc.ChartLocker;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.charts.enc.event.ChartLockerEvent;
import org.knowtiphy.charts.geotools.Coordinates;
import org.knowtiphy.charts.settings.UnitProfile;
import org.knowtiphy.charts.utils.FXUtils;
import org.knowtiphy.charts.utils.ToggleModel;
import org.knowtiphy.shapemap.renderer.ShapeMapRenderer;
import org.knowtiphy.shapemap.renderer.context.SVGCache;
import org.reactfx.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author graham
 */
public class InfoBar extends StackPane
{
  private static double ZOOM_FACTOR = 2;

  private final Label chartScale = new Label();

  private final Label currentExtent = new Label();

  private final Label currentMapSpan = new Label();

  private final Label displayScale = new Label();

  private final Label adjustedDisplayScale = new Label();

  private final Label zoomLevel = new Label();

  private final UnitProfile unitProfile;

  private ENCChart chart;

  private final ChartLocker chartLocker;

  private final MenuButton history;

  private final MapDisplayOptions displayOptions;

  private final SVGCache svgCache;

  private final List<Subscription> subscriptions = new ArrayList<>();

  public InfoBar(
    ToggleModel toggleModel, ENCChart chrt, UnitProfile unitProfile, ChartLocker chartLocker,
    MapDisplayOptions displayOptions, SVGCache svgCache)
  {
    this.chart = chrt;
    this.unitProfile = unitProfile;
    this.chartLocker = chartLocker;
    this.displayOptions = displayOptions;
    this.svgCache = svgCache;

    var zoomIn = new Button("", Fonts.plus());
    zoomIn.setTooltip(new Tooltip("Zoom In"));
    zoomIn.setOnAction(x -> chrt.setZoom(chrt.zoom() + 1));//* ZOOM_FACTOR));
    var zoomOut = new Button("", Fonts.minus());
    zoomOut.setTooltip(new Tooltip("Zoom Out"));
    zoomOut.setOnAction(x -> chrt.setZoom(chrt.zoom() - 1));//* (1 / ZOOM_FACTOR)));

    var mapDisplaySettings = new Button("", Fonts.setting());
    mapDisplaySettings.setTooltip(new Tooltip("Configure Map Visuals"));
    mapDisplaySettings.setOnAction(evt -> toggleModel.toggle());

    var resetViewPort = new Button("", Fonts.resetToOriginalBounds());
    resetViewPort.setTooltip(new Tooltip("Reset Map to Original Dimensions"));
    resetViewPort.setOnAction(x -> {
      try
      {
        chart.setViewPortBounds(chart.bounds());
      }
      catch(TransformException | NonInvertibleTransformException ex)
      {
        Logger.getLogger(InfoBar.class.getName()).log(Level.SEVERE, null, ex);
      }
    });

    history = new MenuButton("", Fonts.history());
    history.setPopupSide(Side.TOP);
    history.setTooltip(new Tooltip("Chart History"));
    var items = new ArrayList<MenuItem>();
    for(var description : chartLocker.history())
    {
      var menuItem = new MenuItem(description.lName() + "  1:" + description.cScale());
      menuItem.setOnAction(event -> loadChart(description));
      items.add(menuItem);
    }
    history.getItems().addAll(items);

    var centerControlsBar = FXUtils.nonResizeable(
      new ToolBar(zoomIn, zoomOut, resetViewPort, new Separator(), history, new Separator(),
        mapDisplaySettings));
    centerControlsBar.getStyleClass().add("controlbar");
    StackPane.setAlignment(centerControlsBar, Pos.BOTTOM_CENTER);

    var leftControlsBar = FXUtils.nonResizeable(new ToolBar(chartScale));
    leftControlsBar.getStyleClass().add("controlbar");
    StackPane.setAlignment(leftControlsBar, Pos.BOTTOM_LEFT);

    var variableLabels = FXUtils.nonResizeable(
      new HBox(currentExtent, currentMapSpan, adjustedDisplayScale, displayScale, zoomLevel));
    variableLabels.getStyleClass().add("infobar");
    StackPane.setAlignment(variableLabels, Pos.BOTTOM_RIGHT);

    getChildren().addAll(leftControlsBar, centerControlsBar, variableLabels);

    showFixedChartInfo();
    showVariableChartInfo();

    widthProperty().addListener(ch -> showVariableChartInfo());
    heightProperty().addListener(ch -> showVariableChartInfo());
    unitProfile.unitChangeEvents().subscribe(ch -> {
      showFixedChartInfo();
      showVariableChartInfo();
    });

    chartLocker.history().addListener((ListChangeListener<ENCCell>) c -> {
      c.next();
      for(var description : c.getAddedSubList())
      {
        var menuItem = new MenuItem(description.lName() + "  1:" + description.cScale());
        menuItem.setOnAction(event -> loadChart(description));
        history.getItems().add(menuItem);
      }
    });

    chartLocker.chartEvents().filter(ChartLockerEvent::isUnload).subscribe(event -> {
      // unsubscribe listeners on the old chart
      subscriptions.forEach(Subscription::unsubscribe);
      subscriptions.clear();
    });

    chartLocker.chartEvents().filter(ChartLockerEvent::isLoad).subscribe(event -> {
      chart = event.chart();
      setupListeners();
      showFixedChartInfo();
      showVariableChartInfo();
    });

    setupListeners();

    ShapeMapRenderer.count.addListener(x -> showFixedChartInfo());
  }

  private void setupListeners()
  {
    subscriptions.add(chart.viewPortBoundsEvent().subscribe(c -> showVariableChartInfo()));
  }

  //  TODO -- needs to go away
  private void showFixedChartInfo()
  {
//    chartName.setText(chart.title());
    chartScale.setText(ShapeMapRenderer.count.get() + ""); // chart.currentScale() +
    // "");
    // chartScale.setText(chart.currentScale() + "");
//    extentLabel.setText(unitProfile.formatEnvelope(chart.bounds()));
  }

  private void showVariableChartInfo()
  {
    var envelope = chart.viewPortBounds();
    var mapWidth = Coordinates.distanceAcross(envelope);
    currentMapSpan.setText(unitProfile.formatDistance(mapWidth, unitProfile::metersToMapUnits));
    // currentScreenToWorld.setText("1" + unitProfile.screenUnit + " : "
    // + Coordinates.twoDec(unitProfile.convertFromScreenUnit(mapWidth /
    // platform.windowWidthCM(this)))
    // + unitProfile.distanceUnit);

    displayScale.setText(chart.displayScale() + "");
    adjustedDisplayScale.setText(chart.adjustedDisplayScale() + "");
    zoomLevel.setText(Coordinates.twoDec(chart.zoom()));
  }

  private void loadChart(ENCCell chartDescription)
  {
//    try
//    {
//      chartLocker.loadChart(chartDescription, displayOptions, svgCache);
//    }
//    catch(TransformException | FactoryException | NonInvertibleTransformException |
//          StyleSyntaxException ex)
//    {
//      Logger.getLogger(InfoBar.class.getName()).log(Level.SEVERE, null, ex);
//    }
  }
}