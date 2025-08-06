/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts;

import java.util.ArrayList;
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
import org.knowtiphy.charts.chartlocker.ChartLocker;
import org.knowtiphy.charts.chartview.ChartViewModel;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.geotools.Coordinates;
import org.knowtiphy.charts.settings.UnitProfile;
import static org.knowtiphy.charts.utils.FXUtils.button;
import static org.knowtiphy.charts.utils.FXUtils.menuButton;
import static org.knowtiphy.charts.utils.FXUtils.nonResizeable;
import org.knowtiphy.charts.utils.ToggleModel;
import static org.knowtiphy.charts.utils.Utils.formatDecimal;
import org.knowtiphy.shapemap.context.SVGCache;

/**
 * The info bar at the bottom of the chart view.
 *
 * <p>TODO -- make this into a region based setup
 */
public class InfoBar extends StackPane {

    private final UnitProfile unitProfile;

    private final ChartViewModel chart;

    private final ChartLocker chartLocker;

    //  will be used when we get the history menu running again
    private final MapDisplayOptions displayOptions;

    private final SVGCache svgCache;

    private final ToggleModel toggleModel;

    private MenuButton history;
    private Label currentExtent;
    private Label currentMapSpan;
    private Label displayScale;
    private Label adjustedDisplayScale;
    private Label zoomLevel;

    public InfoBar(
            ToggleModel toggleModel,
            ChartViewModel chrt,
            UnitProfile unitProfile,
            ChartLocker chartLocker,
            MapDisplayOptions displayOptions,
            SVGCache svgCache) {

        this.toggleModel = toggleModel;
        this.chart = chrt;
        this.unitProfile = unitProfile;
        this.chartLocker = chartLocker;
        this.displayOptions = displayOptions;
        this.svgCache = svgCache;

        initGraphics();
        registerListeners();
    }

    private void initGraphics() {

        currentExtent = new Label();
        currentMapSpan = new Label();
        displayScale = new Label();
        adjustedDisplayScale = new Label();
        zoomLevel = new Label();

        //  the center controls bar
        var controlsBar = nonResizeable(makeControlsBar());
        controlsBar.getStyleClass().add("controlbar");

        //  the information on the left of the info bar
        var leftInfoBar = nonResizeable(new ToolBar(currentExtent));
        leftInfoBar.getStyleClass().add("controlbar");

        //  the information on the right side of the info bar
        var rightLabels =
                nonResizeable(
                        new HBox(currentMapSpan, adjustedDisplayScale, displayScale, zoomLevel));
        rightLabels.getStyleClass().add("infobar");

        //  add the various components to the info bar
        StackPane.setAlignment(controlsBar, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(leftInfoBar, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(rightLabels, Pos.BOTTOM_RIGHT);
        getChildren().addAll(leftInfoBar, controlsBar, rightLabels);

        updateChartInfo();
    }

    private void registerListeners() {
        widthProperty().addListener(_ -> updateChartInfo());
        heightProperty().addListener(_ -> updateChartInfo());
        unitProfile.unitChangeEvents().subscribe(_ -> updateChartInfo());
        chart.viewPortBoundsEvent().subscribe(_ -> updateChartInfo());
        chart.quiltChangeEvent().subscribe(_ -> updateChartInfo());
    }

    private ToolBar makeControlsBar() {

        //  zoom  buttons
        var zoomIn = button(Fonts.plus(), _ -> chart.incZoom(), new Tooltip("Zoom In"));
        var zoomOut = button(Fonts.minus(), _ -> chart.decZoom(), new Tooltip("Zoom Out"));

        //  chart display settings button
        var chartDisplaySettings =
                button(
                        Fonts.setting(),
                        _ -> toggleModel.toggle(),
                        new Tooltip("Configure Map Visuals"));

        //  history menu
        history = menuButton("", Fonts.history(), historyMenuItems(), new Tooltip("Chart History"));
        history.setPopupSide(Side.TOP);

        chartLocker
                .history()
                .addListener(
                        (ListChangeListener<ENCCell>)
                                _ -> history.getItems().setAll(historyMenuItems()));

        return new ToolBar(
                zoomIn, zoomOut, new Separator(), history, new Separator(), chartDisplaySettings);
    }

    //  update the chart information in the info bar
    private void updateChartInfo() {

        currentExtent.setText(unitProfile.formatEnvelope(chart.viewPortBounds()));
        currentMapSpan.setText(
                unitProfile.formatDistance(
                        Coordinates.distanceAcross(chart.viewPortBounds()),
                        unitProfile::metersToMapUnits));
        displayScale.setText("DS = " + formatDecimal(chart.dScale(), 2) + "");
        adjustedDisplayScale.setText(chart.adjustedDisplayScale() + "");
        //        zoomLevel.setText(Coordinates.twoDec(chart.zoom()));
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

    @SuppressWarnings("CallToPrintStackTrace")
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

     //  viewport reset button -- pretty sure this is nonsens
//        @SuppressWarnings("CallToPrintStackTrace")
//        var resetViewPort =
//                button(
//                        Fonts.resetToOriginalBounds(),
//                        _ -> {
//                            try {
//                                chart.reset();
//                            } catch (TransformException | NonInvertibleTransformException ex) {
//                                ex.printStackTrace();
//                            }
//                        },
//                        new Tooltip("Reset Map to Original Dimensions"));