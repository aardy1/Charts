package org.knowtiphy.charts.chartview;

import org.knowtiphy.charts.utils.ScrollState;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.chartview.ChartView.EventModel;
import org.reactfx.Subscription;

/**
 * Support for dragging, panning, and zooming a map, plus positioning a map at some screen
 * coordinates.
 */
public class DragPanZoomSupport {
    //    public static <S, F> Subscription addDragSupport(
    //            EventModel eventModel, BaseMapViewModel<S, F> map) {
    //        var dragState = new DragPanZoomSupport.DragState();
    //
    //        eventModel.mousePressed.subscribe(
    //                event -> {
    //                    dragState.startX = event.getSceneX();
    //                    dragState.startY = event.getSceneY();
    //                });
    //
    //        return eventModel.mouseDragged.subscribe(event -> doDrag(map, event, dragState));
    //    }

    public static <S, F> List<Subscription> addPanningSupport(
            EventModel eventModel, BaseMapViewModel<S, F> map) {
        org.knowtiphy.charts.utils.ScrollState scrollState = new ScrollState();

        var subscriptions = new ArrayList<Subscription>();

        subscriptions.add(
                eventModel
                        .scrollEvents
                        .filter(evt -> evt.getEventType().equals(ScrollEvent.SCROLL_STARTED))
                        .subscribe(evt -> scrollState.isTouchpad = true));
        subscriptions.add(
                eventModel
                        .scrollEvents
                        .filter(evt -> evt.getEventType().equals(ScrollEvent.SCROLL_FINISHED))
                        .subscribe(evt -> scrollState.isTouchpad = false));

        subscriptions.add(
                eventModel
                        .scrollEvents
                        .filter(evt -> evt.getEventType().equals(ScrollEvent.SCROLL))
                        .subscribe(
                                event -> {
                                    if (scrollState.isTouchpad || event.isInertia()) {
                                        var newPos =
                                                new Point2D(event.getDeltaX(), event.getDeltaY());
                                        var result = map.viewPortScreenToWorld().transform(newPos);

                                        var newVPBounds =
                                                new ReferencedEnvelope(map.viewPortBounds());
                                        newVPBounds.translate(
                                                newVPBounds.getMinimum(0) - result.getX(),
                                                newVPBounds.getMaximum(1) - result.getY());

                                        try {
                                            //            var newExtent = clip(map.bounds(),
                                            // newVPBounds, map.crs());
                                            //
                                            // if(!newExtent.equals(map.viewPortBounds()))
                                            //            {
                                            //              map.setViewPortBounds(newExtent);
                                            //            }
                                            map.setViewPortBounds(newVPBounds);
                                        } catch (TransformException
                                                | NonInvertibleTransformException ex) {
                                            Logger.getLogger(DragPanZoomSupport.class.getName())
                                                    .log(Level.SEVERE, null, ex);
                                        }
                                    } else {
                                        //  TODO -- this is a bit hacky
                                        var zoomFactor = event.getDeltaY() > 0 ? 1.05 : 0.95;
                                        map.setZoom(map.zoom() * zoomFactor);
                                    }
                                }));

        return subscriptions;
    }


}