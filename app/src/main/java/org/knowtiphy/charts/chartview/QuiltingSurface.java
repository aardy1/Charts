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
import org.knowtiphy.shapemap.context.RemoveHolesFromPolygon;
import org.knowtiphy.shapemap.context.RenderGeomCache;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

/**
 * @author graham
 */
public class QuiltingSurface extends StackPane {
    private static final Insets INSETS = new Insets(2, 2, 3, 2);

    private final ChartViewModel viewModel;

    private FlowPane controls;
    private Pane displaySurface;

    public QuiltingSurface(ChartViewModel viewModel) {

        this.viewModel = viewModel;
        initGraphics();
        setupListeners();
    }

    private void initGraphics() {

        controls = new FlowPane();
        controls.setVgap(4);
        controls.setHgap(4);
        controls.setPadding(INSETS);
        controls.setAlignment(Pos.BOTTOM_CENTER);
        controls.setPickOnBounds(false);

        displaySurface = new Pane();
        displaySurface.setMouseTransparent(true);
        displaySurface.setPickOnBounds(false);

        getChildren().addAll(displaySurface, controls);
    }

    private void setupListeners() {
        widthProperty().addListener(cl -> makeQuilting());
        heightProperty().addListener(cl -> makeQuilting());
        viewModel.viewPortBoundsEvent().subscribe(extent -> makeQuilting());
    }

    private void makeQuilting() {
        controls.getChildren().clear();
        displaySurface.getChildren().clear();

        //    var intersecting = chartLocker.computeQuilt(chart);
        //    intersecting.sort(Comparator.comparingInt(p -> p.getLeft().cScale()));

        for (var map : viewModel.maps()) {
            var label = new Button(map.cScale() + "");
            label.setFont(Fonts.DEFAULT_FONT_10);
            //      label.setOnAction(eh -> {
            //        try
            //        {
            //          chartLocker.loadChart(cell, displayOptions, svgCache);
            //        }
            //        catch(TransformException | FactoryException | NonInvertibleTransformException
            // |
            //              StyleSyntaxException ex)
            //        {
            //          Logger.getLogger(QuiltingSurface.class.getName()).log(Level.SEVERE, null,
            // ex);
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

    private void showQuilting(Geometry mp) {
        displaySurface.getChildren().clear();

        var tx = new Transformation(viewModel.viewPortWorldToScreen());
        //  TODO -- need a null cache here
        var remover = new RemoveHolesFromPolygon(new RenderGeomCache());

        for (int i = 0; i < mp.getNumGeometries(); i++) {
            //  TODO -- what if its not a polygon -- do what?
            if (mp.getGeometryN(i) instanceof Polygon pl) {
                var polyGeom = remover.apply(pl);
                var polygon = new javafx.scene.shape.Polygon(tx.apply(polyGeom));
                polygon.setFill(Color.BROWN);
                polygon.setOpacity(0.4);
                displaySurface.getChildren().add(polygon);
            }
        }
    }
}