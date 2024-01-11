package org.knowtiphy.charts.chartview;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.chartview.ChartView.EventModel;
import org.knowtiphy.charts.geotools.Coordinates;
import org.knowtiphy.shapemap.model.BaseMapViewModel;
import org.reactfx.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.knowtiphy.charts.geotools.Coordinates.clip;

/**
 * Support for dragging, panning, and zooming a map, plus positioning a map at some screen
 * coordinates.
 */
public class DragPanZoomSupport
{

  public static <S, F> Subscription addDragSupport(
    EventModel eventModel, BaseMapViewModel<S, F> map)
  {

    var dragState = new DragPanZoomSupport.DragState();

    eventModel.mousePressed.subscribe(event -> {
      dragState.startX = event.getSceneX();
      dragState.startY = event.getSceneY();
    });

    return eventModel.mouseDragged.subscribe(event -> doDrag(map, event, dragState));
  }

  public static <S, F> List<Subscription> addPanningSupport(
    EventModel eventModel, BaseMapViewModel<S, F> map)
  {
    var scrollState = new DragPanZoomSupport.ScrollState();

    var subscriptions = new ArrayList<Subscription>();

    subscriptions.add(eventModel.scrollEvents
                        .filter(evt -> evt.getEventType().equals(ScrollEvent.SCROLL_STARTED))
                        .subscribe(evt -> scrollState.isTouchpad = true));
    subscriptions.add(eventModel.scrollEvents
                        .filter(evt -> evt.getEventType().equals(ScrollEvent.SCROLL_FINISHED))
                        .subscribe(evt -> scrollState.isTouchpad = false));

    //@formatter:off
    subscriptions.add(
      eventModel.scrollEvents.filter(evt -> evt.getEventType().equals(ScrollEvent.SCROLL))
                             .subscribe(event -> {

        if(scrollState.isTouchpad || event.isInertia())
        {
          var newPos = new Point2D(event.getDeltaX(), event.getDeltaY());
          var result = map.viewPortScreenToWorld().transform(newPos);

          var newVPBounds = new ReferencedEnvelope(map.viewPortBounds());
                newVPBounds.translate(newVPBounds.getMinimum(0) - result.getX(),
                newVPBounds.getMaximum(1) - result.getY());

          try
          {
            var newExtent = clip(map.bounds(), newVPBounds, map.crs());
            if(!newExtent.equals(map.viewPortBounds()))
            {
              map.setViewPortBounds(newExtent);
            }
          }
          catch(TransformException | NonInvertibleTransformException ex)
          {
            Logger.getLogger(DragPanZoomSupport.class.getName()).log(Level.SEVERE, null, ex);
           }
        }
        else
        {
          //  TODO -- this is a bit hacky
          var zoomFactor = event.getDeltaY() > 0 ? 1.05 : 0.95;
           Coordinates.zoom(map, zoomFactor);
        }
      }));

    //@formatter:on

    return subscriptions;
  }

  public static <S, F> Subscription addZoomSupport(EventModel surface, BaseMapViewModel<S, F> map)
  {
    return surface.zoomEvents.subscribe(event -> {
      // not sure what NaN means, but it can happen
      if(Double.isNaN(event.getZoomFactor()))
      {
        return;
      }

      Coordinates.zoom(map, event.getZoomFactor());
    });
  }

  // need to check for no modifiers at all
  public static <S, F> Subscription addPositionAtSupport(
    EventModel surface, BaseMapViewModel<S, F> map)
  {
    return surface.mouseDoubleClicked.subscribe(event -> {
      try
      {
        Coordinates.positionAt(map, event.getX(), event.getY());
      }
      catch(TransformException | NonInvertibleTransformException ex)
      {
        Logger.getLogger(DragPanZoomSupport.class.getName()).log(Level.SEVERE, null, ex);
      }
    });
  }

  private static class DragState
  {

    double startX;

    double startY;

  }

  private static class ScrollState
  {

    boolean isTouchpad;

  }

  private static <S, F> void doDrag(
    BaseMapViewModel<S, F> map, MouseEvent event, DragState dragState)
  {
    var difX = event.getX() - dragState.startX;
    var difY = event.getY() - dragState.startY;
    dragState.startX = event.getX();
    dragState.startY = event.getY();
    var newPos = new Point2D(difX, difY);
    var result = map.viewPortScreenToWorld().transform(newPos);

    var newVPBounds = new ReferencedEnvelope(map.viewPortBounds());
    newVPBounds.translate(newVPBounds.getMinimum(0) - result.getX(),
      newVPBounds.getMaximum(1) - result.getY());

    try
    {
      var newExtent = clip(map.bounds(), newVPBounds, map.crs());
      if(!newExtent.equals(map.viewPortBounds()))
      {
        map.setViewPortBounds(newExtent);
      }
    }
    catch(TransformException | NonInvertibleTransformException ex)
    {
      Logger.getLogger(DragPanZoomSupport.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}