/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartview;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import org.controlsfx.glyphfont.Glyph;
import org.knowtiphy.charts.enc.ChartLocker;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.charts.enc.event.ChartLockerEvent;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.locationtech.jts.geom.Geometry;
import org.reactfx.Change;
import org.reactfx.EventStream;
import org.reactfx.Subscription;

import java.util.ArrayList;
import java.util.List;

/**
 * @author graham
 */
public class IconSurface extends Pane
{
  private ENCChart chart;

  private final List<Subscription> subscriptions = new ArrayList<>();

  public IconSurface(ChartLocker chartLocker, ENCChart chart)
  {
    this.chart = chart;

    widthProperty().addListener(x -> makeIconLayers());
    heightProperty().addListener(x -> makeIconLayers());

    chartLocker.chartEvents().filter(ChartLockerEvent::isUnload).subscribe(event -> {
      // unsubscribe listeners on the old chart
      subscriptions.forEach(Subscription::unsubscribe);
      subscriptions.clear();
    });

    chartLocker.chartEvents().filter(ChartLockerEvent::isLoad).subscribe(event -> {
      this.chart = event.chart();
      makeIconLayers();
      setupListeners();
    });

    setupListeners();
  }

  private void setupListeners()
  {
    subscriptions.add(chart.viewPortBoundsEvent().subscribe(b -> makeIconLayers()));
    subscriptions.add(chart.layerVisibilityEvent().subscribe(b -> makeIconLayers()));
  }

  public void makeIconLayers()
  {

    getChildren().clear();

    try
    {
      // createIconLayer(S57.OC_CTNARE, Fonts::info, List.of(AT_INFORM), null,
      // null);
      // createIconLayer(S57.OC_MIPARE, Fonts::jet, List.of(AT_INFORM), null, null);
      // createIconLayer(S57.OC_UNSARE, Fonts::info, List.of(AT_INFORM), null,
      // null);
      // createIconLayer(S57.OC_DMPGRD, Fonts::info, List.of(AT_INFORM), null,
      // null);
      // createIconLayer(map, S57.OC_OFSPLF, Fonts::platform, List.of(AT_OBJNAM),
      // "Offshore Platform", displayOptions.showPlatformEvents);
      // // createIconLayer(pane, map, S57.OC_BUAARE);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  // private void createIconLayer(SimpleFeatureType type, Supplier<Glyph> glyph,
  // List<String> attributes,
  // String defaultValue, EventStream<Change<Boolean>> visibilityEvents) throws
  // Exception {
  //
  // var layer = chart.layer(chart.sc);
  // if (layer == null)
  // return;
  //
  // try (var features = layer.getFeatureSource().features()) {
  // while (features.hasNext()) {
  // var g = glyph.get();
  // g.setFontSize(14);
  // createIcon(features.next(), g, attributes, defaultValue, visibilityEvents);
  // }
  // }
  // catch (IOException ex) {
  // //
  // }
  // }

  private void createIcon(
    MemFeature feature, Glyph glyph, List<String> attributes, String defaultValue,
    EventStream<Change<Boolean>> visibilityEvents)
  {

    // var description = description(feature, attributes, defaultValue);
    // if (description == null)
    // return;
    //
    // var geom = (Geometry) feature.getDefaultGeometry();
    // var geomType = ExtraAttributes.geomType(feature);
    // var pt = switch (geomType) {
    // case POINT -> geom;
    // case POLYGON, MULTI_POLYGON -> geom.getInteriorPoint();
    // default -> null;
    // };

    // if (pt == null) {
    // System.err.println("type " + geom.getGeometryType());
    // return;
    // }
    //
    // var tooltip = tooltip(description, Fonts.DEFAULT_FONT_12,
    // Fonts.DEFAULT_FONT_WIDTH_12, 400);
    // glyph.setTooltip(tooltip);
    // var transform = new Transformation(chart.viewPortWorldToScreen());
    //
    // repositionPoint(transform, pt, glyph);
    // getChildren().add(glyph);
    //
    // if (visibilityEvents != null)
    // visibilityEvents.subscribe(c -> glyph.setVisible(c.getNewValue()));
    //
    // switch (geomType) {
    // case LINE_STRING, POLYGON, MULTI_POLYGON -> {
    // var polys = createHighlight(geom, transform, Color.RED);
    // final var fpolys = polys;
    // glyph.addEventHandler(MouseEvent.MOUSE_ENTERED, evt -> later(() ->
    // getChildren().addAll(fpolys)));
    // glyph.addEventHandler(MouseEvent.MOUSE_EXITED, evt -> later(() ->
    // getChildren().removeAll(fpolys)));
    // }
    // default -> {
    // // de nada
    // }
    // }
  }

  private List<Shape> createHighlight(Geometry geom, Transformation transform, Color color)
  {

    var result = new ArrayList<Shape>();
    var boundary = geom.getBoundary();
    for(var i = 0; i < boundary.getNumGeometries(); i++)
    {
      var geomi = boundary.getGeometryN(i);
      // get rid of this as it may make a copy
      var coords = geomi.getCoordinates();
      var pts = new ArrayList<Double>();
      var polygon = new Polyline();
      polygon.setStroke(color);
      polygon.setStrokeWidth(1);
      polygon.setStrokeType(StrokeType.INSIDE);
      // polygon.setClip(this);
      for(var coord : coords)
      {
        transform.apply(coord.x, coord.y);
        pts.add(transform.getX());
        pts.add(transform.getY());
      }

      polygon.getPoints().addAll(pts);
      result.add(polygon);
    }

    return result;
  }

  private static void repositionPoint(Transformation transformation, Geometry point, Region region)
  {
    transformation.apply(point.getCoordinate().x, point.getCoordinate().y);
    region.setTranslateX(transformation.getX());
    region.setTranslateY(transformation.getY());
  }

}