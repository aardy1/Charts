/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.Fonts;
import org.knowtiphy.charts.enc.ChartDescription;
import org.knowtiphy.charts.enc.ChartLocker;
import org.knowtiphy.charts.enc.ENCChart;
import shapemap.style.parser.StyleSyntaxException;

/**
 * @author graham
 */
public class QuiltingSurface extends StackPane {

	private final static Insets INSETS = new Insets(2, 2, 3, 2);

	private final ChartLocker chartLocker;

	private final ENCChart chart;

	private final MapDisplayOptions displayOptions;

	private final BorderPane controlsContainer = new BorderPane();

	private final FlowPane controls = new FlowPane();

	private final Pane displaySurface = new Pane();

	public QuiltingSurface(ChartLocker chartLocker, ENCChart chart, MapDisplayOptions displayOptions) {

		this.chartLocker = chartLocker;
		this.chart = chart;
		this.displayOptions = displayOptions;

		controls.setVgap(4);
		controls.setHgap(4);

		// var separator = new HBox();
		// HBox.setHgrow(separator, Priority.ALWAYS);

		controlsContainer.setPadding(INSETS);
		controlsContainer.setBottom(controls);
		controlsContainer.setPickOnBounds(false);

		displaySurface.setMouseTransparent(true);
		displaySurface.setPickOnBounds(false);

		getChildren().addAll(displaySurface, controlsContainer);

		widthProperty().addListener(cl -> makeQuilting());
		heightProperty().addListener(cl -> makeQuilting());
		chart.viewPortBoundsEvent().subscribe(extent -> makeQuilting());
		chart.newMapEvent().subscribe(extent -> makeQuilting());
	}

	private void makeQuilting() {

		controls.getChildren().clear();
		displaySurface.getChildren().clear();

		var intersecting = new ArrayList<>(chartLocker.intersections(chart.viewPortBounds()));
		Collections.sort(intersecting, (a, b) -> Integer.compare(a.cScale(), b.cScale()));

		// should sort here
		for (var chartDescription : intersecting) {
			if (chartDescription == chart.getChartDescription())
				continue;
			if (chartDescription.getPanels().isEmpty()) {
				System.err.println("EMPTY");
			}
			else {
				var label = new Button(chart.cScale() + "");
				label.setFont(Fonts.DEFAULT_FONT_10);
				label.setOnAction(eh -> {
					try {
						chartLocker.loadChart(chartDescription, displayOptions, // chart.bounds(),
								new Rectangle2D(0, 0, (int) widthProperty().get(), (int) heightProperty().get()));
					}
					catch (TransformException | FactoryException | NonInvertibleTransformException
							| StyleSyntaxException ex) {
						Logger.getLogger(QuiltingSurface.class.getName()).log(Level.SEVERE, null, ex);
					}
				});
				label.setOnMouseEntered(evt -> showQuilting(chartDescription));
				label.setOnMouseExited(evt -> displaySurface.getChildren().clear());
				var color = chart.cScale() > chart.currentScale() ? Color.SPRINGGREEN : Color.LIGHTGREEN;
				label.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
				controls.getChildren().add(label);
			}
		}
	}

	private void showQuilting(ChartDescription chartDescription) {

		displaySurface.getChildren().clear();

		for (var panel : chartDescription.getPanels()) {
			var pts = new double[panel.getVertices().size() * 2];
			for (int i = 0, j = 0; j < pts.length; i++, j += 2) {
				var vertex = panel.getVertices().get(i);
				pts[j] = vertex.x;
				pts[j + 1] = vertex.y;
			}

			var poly = new Polygon(pts);
			poly.setStroke(Color.BLACK);
			poly.setStrokeWidth(5);
			poly.setFill(Color.LIGHTGREY);
			poly.setOpacity(0.4);
			displaySurface.getChildren().add(poly);
		}
	}

}