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
import org.knowtiphy.charts.Fonts;
import org.knowtiphy.charts.enc.ChartLocker;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.charts.enc.event.ChartLockerEvent;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.knowtiphy.shapemap.renderer.context.RemoveHolesFromPolygon;
import org.knowtiphy.shapemap.renderer.context.RenderGeomCache;
import org.knowtiphy.shapemap.renderer.context.SVGCache;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.reactfx.Subscription;

import java.util.ArrayList;
import java.util.List;

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

//    var intersecting = chartLocker.computeQuilt(chart);
//    intersecting.sort(Comparator.comparingInt(p -> p.getLeft().cScale()));

    for(var map : chart.maps())
    {
      var label = new Button(map.cScale() + "");
      label.setFont(Fonts.DEFAULT_FONT_10);
//      label.setOnAction(eh -> {
//        try
//        {
//          chartLocker.loadChart(cell, displayOptions, svgCache);
//        }
//        catch(TransformException | FactoryException | NonInvertibleTransformException |
//              StyleSyntaxException ex)
//        {
//          Logger.getLogger(QuiltingSurface.class.getName()).log(Level.SEVERE, null, ex);
//        }
//      });
      label.setOnMouseEntered(evt -> showQuilting(map.geometry()));
      label.setOnMouseExited(evt -> displaySurface.getChildren().clear());
      var color = map.geometry().isEmpty() ? Color.LIGHTPINK : Color.LIGHTGREEN;
      label.setBackground(
        new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
      controls.getChildren().add(label);
    }
  }

  private void showQuilting(Geometry mp)
  {
    displaySurface.getChildren().clear();

    var tx = new Transformation(chart.viewPortWorldToScreen());
    //  TODO -- need a null cache here
    var remover = new RemoveHolesFromPolygon(new RenderGeomCache());

    for(int i = 0; i < mp.getNumGeometries(); i++)
    {
      //  TODO -- what if its not a polygon -- do what?
      if(mp.getGeometryN(i) instanceof Polygon pl)
      {
        var polyGeom = remover.apply(pl);
        var polygon = new javafx.scene.shape.Polygon(tx.apply(polyGeom));
        polygon.setFill(Color.BROWN);
        polygon.setOpacity(0.4);
        displaySurface.getChildren().add(polygon);
      }
    }
  }

}