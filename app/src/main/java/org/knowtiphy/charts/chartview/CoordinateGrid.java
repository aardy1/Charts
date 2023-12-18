/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartview;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.charts.settings.UnitProfile;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.locationtech.jts.geom.Coordinate;
import org.reactfx.Subscription;

import java.util.ArrayList;
import java.util.List;

/**
 * @author graham
 */
public class CoordinateGrid extends Pane
{
  private ENCChart chart;

  private final UnitProfile unitProfile;

  private final List<Subscription> subscriptions = new ArrayList<>();

  private final Insets LATITUDE_INSET = new Insets(3, 0, 0, 3);

  private final Insets LONGITUDE_INSET = new Insets(3, 0, 0, 3);

  public CoordinateGrid(ENCChart chrt, UnitProfile unitProfile)
  {
    this.chart = chrt;
    this.unitProfile = unitProfile;
    setupListeners();

    //  listeners which don't depend on the chart
    widthProperty().addListener(change -> drawGrid());
    heightProperty().addListener(change -> drawGrid());
    unitProfile.unitChangeEvents().subscribe(e -> drawGrid());
  }

  private void setupListeners()
  {
    subscriptions.forEach(Subscription::unsubscribe);
    subscriptions.clear();
    subscriptions.add(chart.viewPortBoundsEvent().subscribe(extent -> drawGrid()));
    subscriptions.add(chart.newMapViewModel().subscribe(change -> {
      chart = (ENCChart) change.getNewValue();
      setupListeners();
      requestLayout();
    }));
  }

  private void drawGrid()
  {
    System.err.println("Grid layout");
    System.err.println(getWidth());
    System.err.println(getHeight());
    System.err.println(chart.viewPortScreenArea());
    getChildren().clear();

    var transform = new Transformation(chart.viewPortWorldToScreen());
    var delta = getDelta();

    var lines = new ArrayList<Node>();
    var labels = new ArrayList<Node>();
    longitudeLines(transform, delta, lines, labels);
//    latitudeLines(transform, delta, lines, labels);
    var legend = legend(transform, delta);

    getChildren().addAll(lines);
    getChildren().addAll(labels);
    getChildren().add(legend);
  }

  private void longitudeLines(
    Transformation transform, double delta, List<Node> lines, List<Node> labels)
  {
    var extent = chart.viewPortBounds();
    var screenArea = chart.viewPortScreenArea();

    var startLongitude = getStartLongitude(extent);
    for(var longitude = startLongitude; longitude < extent.getMaxX(); longitude += delta)
    {
      longitudeLine(transform, screenArea, longitude, lines, labels);
    }

    for(var longitude = startLongitude - delta; longitude > extent.getMinX(); longitude -= delta)
    {
      longitudeLine(transform, screenArea, longitude, lines, labels);
    }
  }

  private void longitudeLine(
    Transformation transform, Rectangle2D screenArea, double longitude, List<Node> lines,
    List<Node> labels)
  {
    transform.apply(longitude, 0);

    var line = line(0, screenArea.getMinY(), 0, screenArea.getMaxY());
    line.setTranslateX(transform.getX());
    lines.add(line);

    var label = label(unitProfile.formatLongitude(longitude), LONGITUDE_INSET);
    label.setTranslateX(transform.getX());
    label.setTranslateY(label.getBoundsInLocal().getHeight() + 1);
    labels.add(label);
  }

  private void latitudeLines(
    Transformation transform, double delta, List<Node> lines, List<Node> labels)
  {
    var extent = chart.viewPortBounds();
    var screenArea = chart.viewPortScreenArea();

    var startLatitude = getStartLatitude(extent);
    for(var latitude = startLatitude; latitude < extent.getMaxY(); latitude += delta)
    {
      latitudeLine(transform, screenArea, latitude, lines, labels);
    }

    for(var latitude = startLatitude - delta; latitude > extent.getMinY(); latitude -= delta)
    {
      latitudeLine(transform, screenArea, latitude, lines, labels);
    }
  }

  private void latitudeLine(
    Transformation transform, Rectangle2D screenArea, double latitude, List<Node> lines,
    List<Node> labels)
  {
    transform.apply(0, latitude);

    var line = line(screenArea.getMinX(), 0, screenArea.getMaxX(), 0);
    lines.add(line);
    line.setTranslateY(transform.getY());

    var label = label(unitProfile.formatLatitude(latitude), LATITUDE_INSET);
    label.setTranslateY(transform.getY());
    label.setTranslateX(label.getBoundsInLocal().getWidth() + 1);
    labels.add(label);
  }

  private Label label(String title, Insets insets)
  {
    var label = new Label(title);
    label.setPadding(insets);
    return label;
  }

  private Line line(double x1, double y1, double x2, double y2)
  {
    var line = new Line(x1, y1 + 1, x2, y2 - 1);
    line.getStyleClass().add("gridline");
    return line;
  }

  private Node legend(Transformation transform, double delta)
  {
    var bounds = chart.viewPortBounds();

    transform.apply(bounds.getMinX() + delta, 0);

    var line = new Line(0, 0, transform.getX(), 0);
    line.getStyleClass().add("legendline");

    double scaleDistance = 0;
    try
    {
      scaleDistance = getScaleDistance(bounds, delta);
    }
    catch(TransformException ex)
    {
      // ignore
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

  private double getScaleDistance(ReferencedEnvelope bounds, double delta) throws TransformException
  {
    return JTS.orthodromicDistance(new Coordinate(bounds.getMinX(), bounds.getMinY()),
      new Coordinate(bounds.getMinX() + delta, bounds.getMinY()), chart.crs());
  }

  // round the minimum longitude to the closest integer in the viewport bounds
  // minimum longitude is positive => round away from the prime meridian, so 40.4 -> 41
  // minimum longitude is negative => round closer to the prime meridian, -98.7 -> -98
  private int getStartLongitude(ReferencedEnvelope envelope)
  {
    return (int) Math.ceil(envelope.getMinX());
  }

  // round the minimum latitude to the closest integer in the viewport bounds
  // minimum latitude is positive => round closer to the north pole, so 20.4 -> 21
  // minimum latitude is negative => round closer to the equator, -44.7 -> -44
  private int getStartLatitude(ReferencedEnvelope envelope)
  {
    return (int) Math.ceil(envelope.getMinY());
  }

  // get the gap between grid lines
  private double getDelta()
  {
    return chart.viewPortBounds().getWidth() / 10;
  }

}