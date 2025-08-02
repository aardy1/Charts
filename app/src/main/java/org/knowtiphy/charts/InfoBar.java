/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.geometry.Side;
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
import org.knowtiphy.charts.chart.ChartLocker;
import org.knowtiphy.charts.chart.ENCChart;
import org.knowtiphy.charts.chart.event.ChartLockerEvent;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.geotools.Coordinates;
import org.knowtiphy.charts.settings.UnitProfile;
import static org.knowtiphy.charts.utils.FXUtils.button;
import static org.knowtiphy.charts.utils.FXUtils.menuButton;
import static org.knowtiphy.charts.utils.FXUtils.nonResizeable;
import org.knowtiphy.charts.utils.ToggleModel;
import org.knowtiphy.shapemap.renderer.context.SVGCache;
import org.reactfx.Subscription;

/** The info bar at the bottom of the chart view. */
public class InfoBar extends StackPane {
    //    private static double ZOOM_FACTOR = 2;

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

    //  chart listener subscriptions
    private final List<Subscription> subscriptions = new ArrayList<>();

    public InfoBar(
            ToggleModel toggleModel,
            ENCChart chrt,
            UnitProfile unitProfile,
            ChartLocker chartLocker,
            MapDisplayOptions displayOptions,
            SVGCache svgCache) {

        this.chart = chrt;
        this.unitProfile = unitProfile;
        this.chartLocker = chartLocker;
        this.displayOptions = displayOptions;
        this.svgCache = svgCache;

        //  zoom in and out buttons
        var zoomIn =
                button(Fonts.plus(), _ -> chart.setZoom(chart.zoom() + 1), new Tooltip("Zoom In"));
        var zoomOut =
                button(
                        Fonts.minus(),
                        _ -> chart.setZoom(chart.zoom() - 1),
                        new Tooltip("Zoom Out"));

        //  chart display settings button
        var chartDisplaySettings =
                button(
                        Fonts.setting(),
                        _ -> toggleModel.toggle(),
                        new Tooltip("Configure Map Visuals"));

        //  viewport reset button
        var resetViewPort =
                button(
                        Fonts.resetToOriginalBounds(),
                        _ -> {
                            try {
                                chart.setViewPortBounds(chart.bounds());
                            } catch (TransformException | NonInvertibleTransformException ex) {
                                Logger.getLogger(InfoBar.class.getName())
                                        .log(Level.SEVERE, null, ex);
                            }
                        },
                        new Tooltip("Reset Map to Original Dimensions"));

        history = menuButton("", Fonts.history(), historyMenuItems(), new Tooltip("Chart History"));
        history.setPopupSide(Side.TOP);

        //  the buttons in the center of the info bar
        var centerControlsBar =
                nonResizeable(
                        new ToolBar(
                                zoomIn,
                                zoomOut,
                                resetViewPort,
                                new Separator(),
                                history,
                                new Separator(),
                                chartDisplaySettings));
        centerControlsBar.getStyleClass().add("controlbar");
        StackPane.setAlignment(centerControlsBar, Pos.BOTTOM_CENTER);

        //  the information on the left of the info bar
        var leftControlsBar = nonResizeable(new ToolBar(currentExtent));
        leftControlsBar.getStyleClass().add("controlbar");
        StackPane.setAlignment(leftControlsBar, Pos.BOTTOM_LEFT);

        //  the information on the right side of the info bar
        var rightLabels =
                nonResizeable(
                        new HBox(
                                chartScale,
                                currentMapSpan,
                                adjustedDisplayScale,
                                displayScale,
                                zoomLevel));
        rightLabels.getStyleClass().add("infobar");
        StackPane.setAlignment(rightLabels, Pos.BOTTOM_RIGHT);

        //  set the initial chart info
        updateChartInfo();

        //  add the various components to the info bar
        getChildren().addAll(leftControlsBar, centerControlsBar, rightLabels);

        //  when the info bar width or height changes, or  the unit profile changes, update the
        // chart info
        widthProperty().addListener(_ -> updateChartInfo());
        heightProperty().addListener(_ -> updateChartInfo());
        unitProfile.unitChangeEvents().subscribe(_ -> updateChartInfo());

        chartLocker
                .history()
                .addListener(
                        (ListChangeListener<ENCCell>)
                                c -> {
                                    //                                    c.next();
                                    //                                    for (var description :
                                    // c.getAddedSubList()) {
                                    //                                        var menuItem =
                                    //                                                new MenuItem(
                                    //
                                    // description.lname()
                                    //
                                    //  + "  1:"
                                    //
                                    //  + description.cScale());
                                    //
                                    // menuItem.setOnAction(event -> loadChart(description));
                                    //
                                    // history.getItems().add(menuItem);
                                    //                                    }
                                    //                                    menuItem.setOnAction(event
                                    // -> loadChart(description));
                                    history.getItems().setAll(historyMenuItems());
                                });

        chartLocker
                .chartEvents()
                .filter(ChartLockerEvent::isUnload)
                .subscribe(
                        event -> {
                            // unsubscribe listeners on the old chart
                            subscriptions.forEach(Subscription::unsubscribe);
                            subscriptions.clear();
                        });

        chartLocker
                .chartEvents()
                .filter(ChartLockerEvent::isLoad)
                .subscribe(
                        event -> {
                            chart = event.chart();
                            setupListeners();
                            updateChartInfo();
                        });

        setupListeners();
    }

    // when the viewport changes update the variable chart info
    private void setupListeners() {
        subscriptions.add(chart.viewPortBoundsEvent().subscribe(c -> updateChartInfo()));
    }

    //  updated chart information
    private void updateChartInfo() {

        currentExtent.setText(unitProfile.formatEnvelope(chart.bounds()));
        currentMapSpan.setText(
                unitProfile.formatDistance(
                        Coordinates.distanceAcross(chart.viewPortBounds()),
                        unitProfile::metersToMapUnits));
        displayScale.setText(chart.displayScale() + "");
        adjustedDisplayScale.setText(chart.adjustedDisplayScale() + "");
        zoomLevel.setText(Coordinates.twoDec(chart.zoom()));
        //  technically this can't change as the UI is manipulated, so really only needs to be set
        // once
        chartScale.setText(chart.cScale() + "");
    }

    //  the menu items in the history menu
    private ArrayList<MenuItem> historyMenuItems() {

        var items = new ArrayList<MenuItem>();
        for (var description : chartLocker.history()) {
            var menuItem = new MenuItem(description.lname() + "  1:" + description.cScale());
            menuItem.setOnAction(_ -> loadChart(description));
            items.add(menuItem);
        }

        return items;
    }

    //  TODO -- need to update this for quilting
    private void loadChart(ENCCell chartDescription) {
        //        try {
        //            chartLocker.loadChart(chart.bounds(), chartDescription, displayOptions,
        // svgCache);
        //        } catch (TransformException
        //                | FactoryException
        //                | NonInvertibleTransformException
        //                | StyleSyntaxException ex) {
        //            Logger.getLogger(InfoBar.class.getName()).log(Level.SEVERE, null, ex);
        //        }
    }
}