package org.knowtiphy.charts.chartview;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.utils.FXUtils;

/**
 * Support for dragging, panning, and zooming a map, and positioning a map at some screen
 * coordinates.
 */
public class DragPanZoomSupport {

    public static void addZoomSupport(Node mapView, ChartViewModel chart) {

        FXUtils.addZoomHandler(
                mapView,
                event -> {
                    // not sure what NaN means -- something to do with Zoom start/finish
                    if (!Double.isNaN(event.getZoomFactor())) {
                        chart.changeZoomByFactor(event.getZoomFactor());
                    }
                });
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void addDragSupport(Node mapView, ChartViewModel chart) {

        FXUtils.addDragHandler(
                mapView,
                (event, dragState) -> {
                    var diffX = event.getX() - dragState.startX;
                    var diffY = event.getY() - dragState.startY;
                    dragState.startX = event.getX();
                    dragState.startY = event.getY();
                    try {
                        var result =
                                chart.viewPortScreenToWorld().transform(new Point2D(diffX, diffY));
                        var newVPBounds = new ReferencedEnvelope(chart.viewPortBounds());
                        newVPBounds.translate(
                                newVPBounds.getMinimum(0) - result.getX(),
                                newVPBounds.getMaximum(1) - result.getY());

                        chart.setViewPortBounds(newVPBounds);
                    } catch (TransformException | NonInvertibleTransformException ex) {
                        ex.printStackTrace();
                    }
                });
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void addPanningSupport(Node mapView, ChartViewModel chart) {

        FXUtils.addScrollHandler(
                mapView,
                (event, scrollState) -> {
                    if (scrollState.isTouchpad || event.isInertia()) {
                        Point2D result;
                        try {
                            result =
                                    chart.viewPortScreenToWorld()
                                            .transform(
                                                    new Point2D(
                                                            event.getDeltaX(), event.getDeltaY()));
                        } catch (TransformException | NonInvertibleTransformException ex) {
                            ex.printStackTrace();
                            return;
                        }

                        var newVPBounds = new ReferencedEnvelope(chart.viewPortBounds());
                        newVPBounds.translate(
                                newVPBounds.getMinimum(0) - result.getX(),
                                newVPBounds.getMaximum(1) - result.getY());

                        chart.setViewPortBounds(newVPBounds);

                    } else {
                        //  TODO -- this is a bit hacky and needs to be finished
                        var zoomFactor = event.getDeltaY() > 0 ? 1.05 : 0.95;

                        //                            chart.(map.zoom() * zoomFactor);

                    }
                });
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void addPositionAtSupport(Node mapView, ChartViewModel chart) {

        FXUtils.addDoubleClickHandler(
                mapView,
                event -> {
                    try {
                        chart.positionAt(event.getX(), event.getY());
                    } catch (TransformException | NonInvertibleTransformException ex) {
                        ex.printStackTrace();
                    }
                });
    }
}
