/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartview;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.chartlocker.ChartLocker;
import org.knowtiphy.charts.settings.UnitProfile;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.locationtech.jts.geom.Coordinate;

/** A pane to show a lattice of lattitude and longitude lines. */
public class CoordinateGrid extends Pane {

    //  TODO -- should be in CSS
    private static final Insets LATITUDE_INSET = new Insets(3, 0, 0, 3);

    private static final Insets LONGITUDE_INSET = new Insets(3, 0, 0, 3);

    private final ChartViewModel chart;

    private final UnitProfile unitProfile;

    public CoordinateGrid(ChartLocker chartLocker, ChartViewModel chart, UnitProfile unitProfile) {
        this.chart = chart;
        this.unitProfile = unitProfile;
        initGraphics();
        registerListeners();
    }

    private void initGraphics() {
        drawGrid();
    }

    private void registerListeners() {
        widthProperty().addListener(_ -> drawGrid());
        heightProperty().addListener(_ -> drawGrid());
        unitProfile.unitChangeEvents().subscribe(_ -> drawGrid());
        chart.viewPortBoundsEvent().subscribe(_ -> drawGrid());
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void drawGrid() {

        getChildren().clear();

        Transformation wts;
        try {
            chart.calculateTransforms();
            wts = new Transformation(chart.viewPortWorldToScreen());
        } catch (TransformException | NonInvertibleTransformException ex) {
            ex.printStackTrace();
            return;
        }

        var delta = computeLongitudeDelta();

        var lines = new ArrayList<Node>();
        var labels = new ArrayList<Node>();
        createLongitudeLines(wts, delta, lines, labels);
        createLatitudeLines(wts, lines, labels);

        var legend = createLegend(wts, delta);

        getChildren().addAll(lines);
        getChildren().addAll(labels);
        getChildren().add(legend);
    }

    private void createLongitudeLines(
            Transformation transform, double delta, List<Node> lines, List<Node> labels) {

        var extent = chart.viewPortBounds();
        var screenArea = chart.viewPortScreenArea();

        var startLongitude = computeStartLongitude(extent);
        for (var longitude = startLongitude; longitude < extent.getMaxX(); longitude += delta) {
            createLongitudeLine(transform, screenArea, longitude, lines, labels);
        }

        for (var longitude = startLongitude - delta;
                longitude > extent.getMinX();
                longitude -= delta) {
            createLongitudeLine(transform, screenArea, longitude, lines, labels);
        }
    }

    private void createLongitudeLine(
            Transformation transform,
            Rectangle2D screenArea,
            double longitude,
            List<Node> lines,
            List<Node> labels) {
        transform.apply(longitude, 0);

        //  can't be maxY as that produces an infinite loop -- the +/- 5 gives a nice visual
        var line = createLine(0, screenArea.getMinY() + 5, 0, screenArea.getMaxY() - 5);
        line.setTranslateX(transform.getX());
        lines.add(line);

        var label = label(unitProfile.formatLongitude(longitude), LONGITUDE_INSET);
        label.setTranslateX(transform.getX());
        label.setTranslateY(label.getBoundsInLocal().getHeight() + 1);
        labels.add(label);
    }

    private void createLatitudeLines(Transformation wts, List<Node> lines, List<Node> labels) {

        var extent = chart.viewPortBounds();
        var screenArea = chart.viewPortScreenArea();
        var delta = computeLatitudeDelta();

        var startLatitude = computeStartLatitude(extent);
        for (var latitude = startLatitude; latitude < extent.getMaxY(); latitude += delta) {
            createLatitudeLine(wts, screenArea, latitude, lines, labels);
        }

        for (var latitude = startLatitude - delta; latitude > extent.getMinY(); latitude -= delta) {
            createLatitudeLine(wts, screenArea, latitude, lines, labels);
        }
    }

    private void createLatitudeLine(
            Transformation wts,
            Rectangle2D screenArea,
            double latitude,
            List<Node> lines,
            List<Node> labels) {

        wts.apply(0, latitude);

        //  can't be maxX as that produces an infinite loop -- the +/- 5 gives a nice visual
        var line = createLine(screenArea.getMinX() + 5, 0, screenArea.getMaxX() - 5, 0);
        lines.add(line);
        line.setTranslateY(wts.getY());

        var label = label(unitProfile.formatLatitude(latitude), LATITUDE_INSET);
        label.setTranslateY(wts.getY());
        label.setTranslateX(label.getBoundsInLocal().getWidth() + 1);
        labels.add(label);
    }

    private Label label(String title, Insets insets) {
        var label = new Label(title);
        label.setPadding(insets);
        return label;
    }

    private Line createLine(double x1, double y1, double x2, double y2) {
        var line = new Line(x1, y1 + 1, x2, y2 - 1);
        line.getStyleClass().add("gridline");
        return line;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private Node createLegend(Transformation wts, double delta) {

        var bounds = chart.viewPortBounds();

        wts.apply(bounds.getMinX() + delta, 0);
        var line = new Line(0, 0, wts.getX(), 0);
        line.getStyleClass().add("legendline");

        double scaleDistance = 0;
        try {
            scaleDistance = computeScaleDistance(bounds, delta);
        } catch (TransformException ex) {
            ex.printStackTrace();
        }

        var distance = unitProfile.formatDistance(scaleDistance, unitProfile::metersToMapUnits);
        var distanceBox = new Label(distance);
        distanceBox.setAlignment(Pos.CENTER);

        var legend = new VBox(distanceBox, line);
        legend.setAlignment(Pos.CENTER);
        legend.setTranslateX(20);
        //  TODO -- this is hacky, not sure how to do this properly
        legend.setTranslateY(getHeight() - 40);

        return legend;
    }

    private double computeScaleDistance(ReferencedEnvelope bounds, double delta)
            throws TransformException {
        return JTS.orthodromicDistance(
                new Coordinate(bounds.getMinX(), bounds.getMinY()),
                new Coordinate(bounds.getMinX() + delta, bounds.getMinY()),
                chart.crs());
    }

    // round the minimum longitude to the closest integer in the viewport bounds
    // minimum longitude is positive => round away from the prime meridian, so 40.4 -> 41
    // minimum longitude is negative => round closer to the prime meridian, -98.7 -> -98
    private double computeStartLongitude(ReferencedEnvelope envelope) {
        return Math.ceil(envelope.getMinX());
    }

    // round the minimum latitude to the closest integer in the viewport bounds
    // minimum latitude is positive => round closer to the north pole, so 20.4 -> 21
    // minimum latitude is negative => round closer to the equator, -44.7 -> -44
    private double computeStartLatitude(ReferencedEnvelope envelope) {
        return Math.ceil(envelope.getMinY());
    }

    // get the gap between grid lines
    private double computeLongitudeDelta() {
        return chart.viewPortBounds().getWidth() / 10;
    }

    private double computeLatitudeDelta() {
        return chart.viewPortBounds().getHeight() / 10;
    }
}