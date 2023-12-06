package org.knowtiphy.charts.chartview;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.chartview.ChartView.EventModel;
import org.knowtiphy.charts.geotools.Coordinates;
import org.knowtiphy.shapemap.api.model.MapViewModel;
import org.reactfx.Subscription;

import static org.knowtiphy.charts.geotools.Coordinates.clip;

/**
 * Support for dragging, panning, and zooming a map, plus positioning a map at some screen
 * coordinates.
 */
public class DragPanZoomSupport {

	public static Subscription addDragSupport(EventModel eventModel, MapViewModel map) {

		var dragState = new DragPanZoomSupport.DragState();

		eventModel.mousePressed.subscribe(event -> {
			dragState.startX = event.getSceneX();
			dragState.startY = event.getSceneY();
		});

		// eventModel.mouseDragStarted.subscribe(event -> doDrag(map, event, dragState));
		return eventModel.mouseDragged.subscribe(event -> doDrag(map, event, dragState));
	}

	public static Subscription addPanningSupport(EventModel eventModel, MapViewModel map) {

		// eventModel.scrollEvents.filter(event -> event.getTouchCount() ==
		// 0).subscribe(event -> {
		// System.err.println("Scrolling Wheel " + event.getTouchCount());
		//
		// // Coordinates.zoom(map, 1.0000005);
		// });

		return eventModel.scrollEvents.subscribe(event -> {// filter(event ->
															// event.getTouchCount() !=
															// 0).subscribe(event -> {

			System.err.println("Scrolling " + event.getTouchCount());
			// this seems kind of bogus
			var newPos = new Point2D(event.getDeltaX(), event.getDeltaY());
			var result = map.viewPortScreenToWorld().transform(newPos);

			var newVPBounds = new ReferencedEnvelope(map.viewPortBounds());
			newVPBounds.translate(newVPBounds.getMinimum(0) - result.getX(), newVPBounds.getMaximum(1) - result.getY());

			try {
				var newExtent = clip(map.bounds(), newVPBounds, map.crs());
				if (!newExtent.equals(map.viewPortBounds())) {
					map.setViewPortBounds(newExtent);
				}
			}
			catch (TransformException | NonInvertibleTransformException ex) {
				Logger.getLogger(DragPanZoomSupport.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
	}

	public static Subscription addZoomSupport(EventModel surface, MapViewModel map) {
		return surface.zoomEvents.subscribe(event -> {
			// not sure what NaN means, but it can happen
			if (Double.isNaN(event.getZoomFactor())) {
				return;
			}

			Coordinates.zoom(map, event.getZoomFactor());
		});
	}

	// need to check for no modifiers at all
	public static Subscription addPositionAtSupport(EventModel surface, MapViewModel map) {
		return surface.mouseDoubleClicked.subscribe(event -> {
			try {
				Coordinates.positionAt(map, event.getX(), event.getY());
			}
			catch (TransformException | NonInvertibleTransformException ex) {
				Logger.getLogger(DragPanZoomSupport.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
	}

	private static class DragState {

		double startX;

		double startY;

	}

	private static void doDrag(MapViewModel map, MouseEvent event, DragState dragState) {

		var difX = event.getX() - dragState.startX;
		var difY = event.getY() - dragState.startY;
		dragState.startX = event.getX();
		dragState.startY = event.getY();
		var newPos = new Point2D(difX, difY);
		var result = map.viewPortScreenToWorld().transform(newPos);

		var newVPBounds = new ReferencedEnvelope(map.viewPortBounds());
		newVPBounds.translate(newVPBounds.getMinimum(0) - result.getX(), newVPBounds.getMaximum(1) - result.getY());

		try {
			var newExtent = clip(map.bounds(), newVPBounds, map.crs());
			if (!newExtent.equals(map.viewPortBounds())) {
				map.setViewPortBounds(newExtent);
			}
		}
		catch (TransformException | NonInvertibleTransformException ex) {
			Logger.getLogger(DragPanZoomSupport.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}