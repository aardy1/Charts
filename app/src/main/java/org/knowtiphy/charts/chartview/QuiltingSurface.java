/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartview;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.NonInvertibleTransformException;
import org.apache.commons.lang3.tuple.Pair;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.Fonts;
import org.knowtiphy.charts.enc.ChartLocker;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.charts.enc.event.ChartLockerEvent;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.knowtiphy.shapemap.renderer.context.RemoveHolesFromPolygon;
import org.knowtiphy.shapemap.renderer.context.RenderGeomCache;
import org.knowtiphy.shapemap.renderer.context.SVGCache;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.reactfx.Subscription;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author graham
 */
public class QuiltingSurface extends StackPane
{
  private static final Insets INSETS = new Insets(2, 2, 3, 2);

  private final ChartLocker chartLocker;

  private ENCChart chart;

  private final MapDisplayOptions displayOptions;

  private final SVGCache svgCache;

  private final FlowPane controls = new FlowPane();

  private final Pane displaySurface = new Pane();

  private final List<Subscription> subscriptions = new ArrayList<>();

  public QuiltingSurface(
    ChartLocker chartLocker, ENCChart chart, MapDisplayOptions displayOptions, SVGCache svgCache)
  {
    this.chartLocker = chartLocker;
    this.chart = chart;
    this.displayOptions = displayOptions;
    this.svgCache = svgCache;
    controls.setVgap(4);
    controls.setHgap(4);

    // var separator = new HBox();
    // HBox.setHgrow(separator, Priority.ALWAYS);

    controls.setPadding(INSETS);
    controls.setAlignment(Pos.BOTTOM_CENTER);
    controls.setPickOnBounds(false);

    displaySurface.setMouseTransparent(true);
    displaySurface.setPickOnBounds(false);

    getChildren().addAll(displaySurface, controls);

    widthProperty().addListener(cl -> makeQuilting());
    heightProperty().addListener(cl -> makeQuilting());

    chartLocker.chartEvents().filter(ChartLockerEvent::isUnload).subscribe(event -> {
      subscriptions.forEach(Subscription::unsubscribe);
      subscriptions.clear();
    });

    chartLocker.chartEvents().filter(ChartLockerEvent::isLoad).subscribe(change -> {
      this.chart = change.chart();
      setupListeners();
      makeQuilting();
    });

    setupListeners();
  }

  private void setupListeners()
  {
    subscriptions.add(chart.viewPortBoundsEvent().subscribe(extent -> makeQuilting()));
  }

  private void makeQuilting()
  {
    controls.getChildren().clear();
    displaySurface.getChildren().clear();

    var intersecting = chartLocker.computeQuilt(chart);
    intersecting.sort(Comparator.comparingInt(p -> p.getLeft().cScale()));
    System.err.println("quilt = " + intersecting);
    for(var foo : intersecting)
    {
      var cell = foo.getLeft();
//      System.err.println(cell);
//      System.err.println(foo.getRight());
//      if(cell.equals(chart.cell()))
//      {
//        continue;
//      }

      var label = new Button(cell.cScale() + "");
      label.setFont(Fonts.DEFAULT_FONT_10);
      label.setOnAction(eh -> {
        try
        {
          chartLocker.loadChart(cell, displayOptions, svgCache);
        }
        catch(TransformException | FactoryException | NonInvertibleTransformException |
              StyleSyntaxException ex)
        {
          Logger.getLogger(QuiltingSurface.class.getName()).log(Level.SEVERE, null, ex);
        }
      });
      label.setOnMouseEntered(evt -> showQuilting(foo));
      label.setOnMouseExited(evt -> displaySurface.getChildren().clear());
      var color = chart.cScale() > chart.displayScale() ? Color.LIGHTPINK : Color.LIGHTGREEN;
      label.setBackground(
        new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
      controls.getChildren().add(label);
    }
  }

  private void showQuilting(Pair<ENCCell, Geometry> cell)
  {
    displaySurface.getChildren().clear();

    var tx = new Transformation(chart.viewPortWorldToScreen());

    var mp = cell.getRight();
    for(int i = 0; i < mp.getNumGeometries(); i++)
    {
      if(mp.getGeometryN(i) instanceof Polygon)
      {
        var polygon = (Polygon) mp.getGeometryN(i);
        var removed = new RemoveHolesFromPolygon(new RenderGeomCache()).apply(polygon);
        var pts = tx.apply(removed);
        var poly = new javafx.scene.shape.Polygon(pts);
        poly.setFill(Color.RED);
        poly.setOpacity(0.4);
        displaySurface.getChildren().add(poly);
      }
    }
//    System.err.println("\tcell #panel = " + cell.panels().size());

//      System.err.println("\tcell panel extent = " + panel.geom());
//      System.err.println(
//        "\tintersects = " + panel.geom().intersects(JTS.toGeometry(chart.viewPortBounds())));

//    var pts = new double[panel.vertices().size() * 2];
//    for(int i = 0, j = 0; j < pts.length; i++, j += 2)
//    {
//      var vertex = panel.vertices().get(i);
//      pts[j] = vertex.x;
//      pts[j + 1] = vertex.y;
//    }
//
//    //  TODO -- need to clip here
//    tx.apply(cell.getRight());
//    var poly = new Polygon(pts);
//    poly.setFill(Color.RED);
//    poly.setOpacity(0.4);
//    displaySurface.getChildren().add(poly);

//    for(var panel : cell.panels())
//    {
////      System.err.println("\tcell panel extent = " + panel.geom());
////      System.err.println(
////        "\tintersects = " + panel.geom().intersects(JTS.toGeometry(chart.viewPortBounds())));
//
//      var pts = new double[panel.vertices().size() * 2];
//      for(int i = 0, j = 0; j < pts.length; i++, j += 2)
//      {
//        var vertex = panel.vertices().get(i);
//        pts[j] = vertex.x;
//        pts[j + 1] = vertex.y;
//      }
//
//      //  TODO -- need to clip here
//      tx.apply(pts);
//      var poly = new Polygon(pts);
//      poly.setFill(Color.RED);
//      poly.setOpacity(0.4);
//      displaySurface.getChildren().add(poly);
//    }
  }

}