/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartview;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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

import static org.knowtiphy.charts.geotools.Coordinates.labelLattitude;
import static org.knowtiphy.charts.geotools.Coordinates.labelLongitude;

/**
 * @author graham
 */
public class CoordinateGrid extends Pane
{

  private ENCChart chart;

  private final UnitProfile unitProfile;

  private final List<Subscription> subscriptions = new ArrayList<>();

  private final Insets LATTITUDE_INSET = new Insets(3, 0, 0, 3);

  private final Insets LONGITUDE_INSET = new Insets(3, 0, 0, 3);

  public CoordinateGrid(ENCChart chrt, UnitProfile unitProfile)
  {

    this.chart = chrt;
    this.unitProfile = unitProfile;
    setupListeners();
  }

  private void setupListeners()
  {
    subscriptions.forEach(s -> s.unsubscribe());
    subscriptions.clear();
    subscriptions.add(chart.viewPortBoundsEvent().subscribe(extent -> requestLayout()));
    subscriptions.add(chart.newMapViewModel().subscribe(change -> {
      chart = (ENCChart) change.getNewValue();
      setupListeners();
      requestLayout();
    }));
  }

  @Override
  public void layoutChildren()
  {

    System.err.println("Yeah baby");
    getChildren().clear();

    var transform = new Transformation(chart.viewPortWorldToScreen());
    var delta = getDelta();

    var toAdd = new ArrayList<Node>();
    longitudeLines(transform, delta, toAdd);
    lattitudeLines(transform, delta, toAdd);
    // toAdd.add(makeLegend(transform, delta));
    getChildren().addAll(toAdd);
  }

  private void longitudeLines(Transformation transform, double delta, List<Node> toAdd)
  {

    var extent = chart.viewPortBounds();
    var screenArea = chart.viewPortScreenArea();

    var startLongitude = getStartLongitude(extent);
    for(double longitude = startLongitude; longitude < extent.getMaxX(); longitude += delta)
    {
      var line = makeLine(0, screenArea.getMinY(), 0, screenArea.getMaxY());
      var label = makeLabel(labelLongitude(longitude), LONGITUDE_INSET);
      transform.apply(longitude, 0);
      line.setTranslateX(transform.getX());
      label.setTranslateX(transform.getX());
      toAdd.add(line);
      toAdd.add(label);
    }

    for(double longitude = startLongitude - delta; longitude > extent.getMinX(); longitude -= delta)
    {
      var line = makeLine(0, screenArea.getMinY(), 0, screenArea.getMaxY());
      var label = makeLabel(labelLongitude(longitude), LONGITUDE_INSET);
      transform.apply(longitude, 0);
      line.setTranslateX(transform.getX());
      label.setTranslateX(transform.getX());
      toAdd.add(line);
      toAdd.add(label);
    }
  }

  private void lattitudeLines(Transformation transform, double delta, List<Node> toAdd)
  {

    var extent = chart.viewPortBounds();
    var screenArea = chart.viewPortScreenArea();

    var startLattitude = getStartLattitude(extent);
    for(double lattitude = startLattitude; lattitude < extent.getMaxY(); lattitude += delta)
    {
      var line = makeLine(screenArea.getMinX(), 0, screenArea.getMaxX(), 0);
      var label = makeLabel(labelLattitude(lattitude), LATTITUDE_INSET);
      transform.apply(0, lattitude);
      line.setTranslateY(transform.getY());
      label.setTranslateY(transform.getY());
      toAdd.add(line);
      toAdd.add(label);
    }

    for(double lattitude = startLattitude - delta; lattitude > extent.getMinY(); lattitude -= delta)
    {
      var line = makeLine(screenArea.getMinX(), 0, screenArea.getMaxX(), 0);
      var label = makeLabel(labelLattitude(lattitude), LATTITUDE_INSET);
      transform.apply(0, lattitude);
      line.setTranslateY(transform.getY());
      label.setTranslateY(transform.getY());
      toAdd.add(line);
      toAdd.add(label);
    }
  }

  private Label makeLabel(String title, Insets insets)
  {
    var label = new Label(title);
    label.setPadding(insets);
    return label;
  }

  private Line makeLine(double x1, double y1, double x2, double y2)
  {
    var line = new Line(x1, y1, x2, y2);
    line.getStyleClass().add("gridline");
    return line;
  }

  private VBox makeLegend(Transformation transform, double delta)
  {

    var bounds = chart.viewPortBounds();

    var legend = new VBox();
    transform.apply(bounds.getMinX() + delta, 0);
    var line = new Line(0, 0, transform.getX(), 0);
    line.setTranslateX(20);
    line.getStyleClass().add("legendline");
    legend.getChildren().add(line);

    try
    {
      var scaleDistance = getScaleDistance(bounds, delta);
      var distanceBox = new HBox(
        new Label(unitProfile.metersToMapUnits(scaleDistance) + " " + unitProfile.distanceUnit()));
      distanceBox.setAlignment(Pos.TOP_RIGHT);
      legend.getChildren().add(distanceBox);
    }
    catch(TransformException ex)
    {
      // ignore
    }

    legend.setTranslateX(20);
    legend.setTranslateY(getBoundsInLocal().getMaxY() - 40);

    return legend;
  }

  public double getScaleDistance(ReferencedEnvelope bounds, double delta) throws TransformException
  {
    return JTS.orthodromicDistance(new Coordinate(bounds.getMinX(), bounds.getMinY()),
      new Coordinate(bounds.getMinX() + delta, bounds.getMinY()), chart.crs()) / 1000;
  }

  // round the minimum longitude to the closest integer in the viewport bounds
  // minimum longitude is positive => round away from the prime meridian, so 40.4 -> 41
  // minimum longitude is negative => round closer to the prime meridian, -98.7 -> -98
  private int getStartLongitude(ReferencedEnvelope envelope)
  {
    return (int) Math.ceil(envelope.getMinX());
  }

  // round the minimum lattitude to the closest integer in the viewport bounds
  // minimum lattitude is positive => round closer to the north pole, so 20.4 -> 21
  // minimum lattitude is negative => round closer to the equator, -44.7 -> -44
  private int getStartLattitude(ReferencedEnvelope envelope)
  {
    return (int) Math.ceil(envelope.getMinY());
  }

  // get the gap between grid lines
  private double getDelta()
  {
    return chart.viewPortBounds().getWidth() / 10;
  }

}