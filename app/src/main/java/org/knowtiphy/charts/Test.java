/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts;

/**
 * @author graham
 */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

/**
 * Prompts the user for a shapefile and displays the contents on the screen in a map frame.
 *
 * <p>This is the GeoTools Quickstart application used in documentationa and tutorials. *
 */
public class Test extends Application {

    static GeometryFactory GF = new GeometryFactory();

    static Coordinate[] exteriorCoords =
            new Coordinate[] {
                new Coordinate(400, 500), //
                new Coordinate(100, 400),
                new Coordinate(200, 200),
                new Coordinate(300, 100), //
                new Coordinate(500, 300),
                new Coordinate(400, 500)
            };

    static Coordinate[] hole1 =
            new Coordinate[] {
                new Coordinate(300, 300), //
                new Coordinate(350, 350),
                new Coordinate(400, 299),
                new Coordinate(300, 300)
            };

    static Coordinate[] hole2 =
            new Coordinate[] {
                new Coordinate(150, 375), //
                new Coordinate(250, 375),
                new Coordinate(250, 350),
                new Coordinate(150, 350),
                new Coordinate(150, 375)
            };

    static Coordinate[] hole3 =
            new Coordinate[] {
                new Coordinate(330, 400), //
                new Coordinate(350, 400),
                new Coordinate(350, 360),
                new Coordinate(330, 370),
                new Coordinate(330, 400)
            };

    /**
     * GeoTools Quickstart demo application. Prompts the user for a shapefile and displays its
     * contents on the screen in a map frame
     */
    public void start(Stage primaryStage) throws Exception {

        var width = 1200;
        var height = 800;

        var canvas = new Canvas(width, height);
        var gc = canvas.getGraphicsContext2D();
        // gc.setFill(Color.BLUE);
        // gc.fillRect(0, 0, width, height);
        // var exterior = GF.createLinearRing(exteriorCoords);
        // var poly = GF.createPolygon(exterior, new LinearRing[] {
        // GF.createLinearRing(hole3), GF.createLinearRing(hole1),
        // GF.createLinearRing(hole2) });

        // var renderGeom = remove(poly);
        // System.err.println("poly = " + renderGeom);
        // drawPoly(gc, renderGeom);

        // LoaderParameters params = new LoaderParameters();
        // params.styleSheets = "/Users/graham/Desktop/foo.css";
        // var image = SVGLoader.load(new
        // File("/Users/graham/Desktop/foo.svg").toURI().toURL());
        // var img = image.toImage(ScaleQuality.RENDER_QUALITY, 12);
        //
        // gc.drawImage(img, 100, 200, 12, 12);

        Polyline polyline = new Polyline();
        polyline.getPoints().addAll(new Double[] {0.0, 0.0, 20.0, 10.0, 10.0, 20.0});
        var scene = new Scene(new Pane(polyline), width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void drawPoly(GraphicsContext gc, Polygon poly) {

        var xs = new double[poly.getExteriorRing().getNumPoints()];
        var ys = new double[poly.getExteriorRing().getNumPoints()];
        for (int i = 0; i < poly.getExteriorRing().getNumPoints(); i++) {
            xs[i] = poly.getExteriorRing().getCoordinateN(i).x;
            ys[i] = poly.getExteriorRing().getCoordinateN(i).y;
        }

        gc.fillPolygon(xs, ys, xs.length);
    }

    public static void main(String[] args) {
        Application.launch(Test.class, args);
    }
}
