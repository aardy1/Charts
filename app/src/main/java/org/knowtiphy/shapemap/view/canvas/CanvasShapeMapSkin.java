package org.knowtiphy.shapemap.view.canvas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.chartview.ChartView.EventModel;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.shapemap.renderer.ShapeMapRenderer;
import org.knowtiphy.shapemap.renderer.context.RemoveHolesFromPolygon;
import org.knowtiphy.shapemap.renderer.context.RendererContext;
import org.knowtiphy.shapemap.view.ShapeMapBaseSkin;
import org.knowtiphy.shapemap.view.ShapeMapView;
import org.reactfx.Subscription;

public class CanvasShapeMapSkin extends ShapeMapBaseSkin {

	private static final double PREFERRED_WIDTH = Region.USE_COMPUTED_SIZE;

	private static final double PREFERRED_HEIGHT = Region.USE_COMPUTED_SIZE;

	private ENCChart chart;

	private final Pane root;

	private final BorderPane borderPane;

	private final List<Subscription> subscriptions = new ArrayList<>();

	public CanvasShapeMapSkin(ShapeMapView surface, ENCChart chart, EventModel eventModel) {
		super(surface);

		this.chart = chart;
		borderPane = new BorderPane();
		root = new Pane(borderPane);
		getChildren().addAll(root);

		initGraphics();

		root.addEventHandler(MouseEvent.ANY, (MouseEvent event) -> {
			eventModel.mouseEvents.push(event);
		});

		setupListeners();
	}

	private void setupListeners() {
		// unsubscribe listeners on the old chart
		subscriptions.forEach(s -> s.unsubscribe());
		subscriptions.clear();
		subscriptions.add(chart.viewPortBoundsEvent.subscribe(b -> root.requestLayout()));
		subscriptions.add(chart.layerVisibilityEvent.subscribe(b -> root.requestLayout()));
		subscriptions.add(chart.newChartEvents.subscribe(newChart -> {
			this.chart = newChart;
			setupListeners();
			root.requestLayout();
		}));
	}

	private void initGraphics() {
		if (Double.compare(S().getPrefWidth(), 0.0) <= 0 || Double.compare(S().getPrefHeight(), 0.0) <= 0
				|| Double.compare(S().getWidth(), 0.0) <= 0 || Double.compare(S().getHeight(), 0.0) <= 0) {
			if (S().getPrefWidth() > 0 && S().getPrefHeight() > 0) {
				S().setPrefSize(S().getPrefWidth(), S().getPrefHeight());
			}
			else {
				S().setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
			}
		}
	}

	@Override
	public void layoutChildren(final double x, final double y, final double width, final double height) {
		super.layoutChildren(x, y, width, height);
		repaint();
	}

	private void repaint() {

		var width = (int) root.getWidth();
		var height = (int) root.getHeight();

		var canvas = new Canvas(width, height);
		var graphics = canvas.getGraphicsContext2D();
		graphics.setFill(Color.LIGHTGREY);
		graphics.fillRect(0, 0, width, height);

		var renderablePolygonProvider = new RemoveHolesFromPolygon(chart.renderGeomCache());
		var rendererContext = new RendererContext(renderablePolygonProvider, chart.svgCache());
		var renderer = new ShapeMapRenderer(chart, rendererContext, graphics);
		try {
			renderer.paint(new Rectangle2D(0, 0, width, height), chart.viewPortBounds());
			borderPane.setCenter(canvas);
		}
		catch (IOException | NonInvertibleTransformException | FactoryException | TransformException ex) {
			ex.printStackTrace(System.err);
		}
	}
	//
	// private class MyThreadFactory implements ThreadFactory {
	//
	// @Override
	// public Thread newThread(Runnable r) {
	// var thread = new Thread(r);
	// thread.setDaemon(true);
	// return thread;
	// }
	//
	// }

}
