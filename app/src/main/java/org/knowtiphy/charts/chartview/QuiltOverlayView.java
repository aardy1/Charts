/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartview;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.Fonts;
import org.knowtiphy.shapemap.context.ComputeRenderableGeometry;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

/**
 * A view of the current quilt that shows each cell in the quilt as a simple geometry with no
 * features.
 */
public class QuiltOverlayView extends StackPane {

    private static final Insets INSETS = new Insets(2, 2, 3, 2);

    private final ChartViewModel viewModel;

    private FlowPane cellNames;
    private Pane overlaySurface;

    public QuiltOverlayView(ChartViewModel viewModel) {

        this.viewModel = viewModel;
        initGraphics();
        setupListeners();
    }

    private void initGraphics() {

        cellNames = new FlowPane();
        cellNames.setVgap(4);
        cellNames.setHgap(4);
        cellNames.setPadding(INSETS);
        cellNames.setAlignment(Pos.BOTTOM_CENTER);
        cellNames.setPickOnBounds(false);

        overlaySurface = new Pane();
        overlaySurface.setMouseTransparent(true);
        overlaySurface.setPickOnBounds(false);

        getChildren().addAll(overlaySurface, cellNames);
    }

    private void setupListeners() {
        widthProperty().addListener(_foo -> createQuiltOverlay());
        heightProperty().addListener(_foo -> createQuiltOverlay());
        viewModel.quiltChangeEvent().subscribe(_foo -> createQuiltOverlay());
        viewModel.viewPortBoundsEvent().subscribe(_foo -> createQuiltOverlay());
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void createQuiltOverlay() {

        cellNames.getChildren().clear();
        overlaySurface.getChildren().clear();

        for (var map : viewModel.maps()) {

            var label = new Label(map.cScale() + "");
            label.setFont(Fonts.DEFAULT_FONT_10);
            var color = map.geometry().isEmpty() ? Color.LIGHTPINK : Color.LIGHTGREEN;
            label.setBackground(
                    new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

            label.setOnMouseEntered(_foo -> showQuiltGeometry(map.geometry()));
            label.setOnMouseExited(_foo -> overlaySurface.getChildren().clear());

            cellNames.getChildren().add(label);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void showQuiltGeometry(Geometry geom) {

        overlaySurface.getChildren().clear();

        Transformation tx;
        try {
            tx = new Transformation(viewModel.viewPortWorldToScreen());
        } catch (TransformException | NonInvertibleTransformException ex) {
            ex.printStackTrace(System.err);
            return;
        }

        //  TODO -- need a null catch here

        for (int i = 0; i < geom.getNumGeometries(); i++) {
            //  TODO -- what if its not a polygon -- do what? Is that even possible?
            if (geom.getGeometryN(i) instanceof Polygon pl) {
                var polyGeom = ComputeRenderableGeometry.compute(pl);
                var polygon = new javafx.scene.shape.Polygon(tx.apply(polyGeom));
                polygon.setFill(Color.BROWN);
                polygon.setOpacity(0.4);
                overlaySurface.getChildren().add(polygon);
            } else System.out.println("NOT A POLY");
        }
    }
}