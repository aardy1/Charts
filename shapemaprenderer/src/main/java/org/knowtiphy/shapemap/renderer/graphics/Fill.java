/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.graphics;

import static org.knowtiphy.shapemap.api.FeatureGeomType.MULTI_LINE_STRING;
import static org.knowtiphy.shapemap.api.FeatureGeomType.MULTI_POLYGON;
import static org.knowtiphy.shapemap.api.FeatureGeomType.POINT;
import org.knowtiphy.shapemap.api.RenderableGeometry;
import org.knowtiphy.shapemap.renderer.RenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

public class Fill {

    /**
     * Setup the fill values for a graphics context from fill information.
     *
     * @param context the rendering context
     * @param fillInfo the fill information
     */
    public static void setup(RenderingContext<?> context, FillInfo fillInfo) {
        context.graphicsContext().setFill(fillInfo.fill());
        context.graphicsContext().setGlobalAlpha(fillInfo.opacity());
    }

    /**
     * Called by a polygon symbolizer to fill a feature's geometry. Note that polygon symbolizers
     * can be used on any geometry.
     *
     * @param <F> the type of the feature
     * @param context the rendering context
     * @param feature the feature to render
     */
    public static <F> void fill(RenderingContext<F> context, F feature) {

        var renderableGeom = context.renderablePolygonProvider().getRenderableGeometry(feature);

        if (renderableGeom == null) {
            //  fallback to using the geometries directly
            fill(context, context.featureAdapter().defaultGeometry(feature));
        } else {
            switch (context.featureAdapter().geomType(feature)) {
                case POINT, MULTI_POINT -> fillPoint(context, renderableGeom);
                case LINE_STRING, LINEAR_RING, MULTI_LINE_STRING, POLYGON, MULTI_POLYGON ->
                        fillAsPolygon(context, renderableGeom);
                default ->
                        throw new IllegalArgumentException(
                                "Unknown geometry type "
                                        + context.featureAdapter().geomType(feature));
            }
        }
    }

    private static void fillPoint(RenderingContext<?> context, RenderableGeometry renderableGeom) {
        assert renderableGeom != null;

        for (var shape : renderableGeom.forFill()) {
            var x = shape.xs()[0];
            var y = shape.ys()[0];
            context.graphicsContext().fillRect(x, y, context.onePixelX(), context.onePixelY());
        }
    }

    private static void fillAsPolygon(
            RenderingContext<?> context, RenderableGeometry renderableGeometry) {

        for (var shape : renderableGeometry.forFill()) {
            var xs = shape.xs();
            var ys = shape.ys();
            context.graphicsContext().fillPolygon(xs, ys, xs.length);
        }
    }

    // if we don't have a renderable geometry for a geometry then fall back to dynamically
    // converting geometries to renderable geometries as we go
    private static void fill(RenderingContext<?> context, Geometry geom) {

        switch (context.featureAdapter().geomType(geom)) {
            case LINE_STRING, LINEAR_RING, POLYGON, MULTI_LINE_STRING, MULTI_POLYGON ->
                    fillAsPolygon(
                            context,
                            context.renderablePolygonProvider().getRenderableGeometry(geom));
            case POINT -> fillPoint(context, (Point) geom);
            case MULTI_POINT -> {
                for (int i = 0; i < geom.getNumGeometries(); i++) {
                    fillPoint(context, (Point) geom.getGeometryN(i));
                }
            }
            default ->
                    throw new IllegalArgumentException(
                            "Unknown geometry type " + context.featureAdapter().geomType(geom));
        }
    }

    private static void fillPoint(RenderingContext<?> context, Point point) {
        context.graphicsContext()
                .fillRect(point.getX(), point.getY(), context.onePixelX(), context.onePixelY());
    }
}

  //                var renderGeom =
            // context.renderablePolygonProvider().getRenderableGeometry(geom);
            //                for (var poly : renderGeom.forFill()) {
            //                    var xs = poly.xs();
            //                    var ys = poly.ys();
            //                    context.graphicsContext().fillPolygon(xs, ys, xs.length);
            //                }
            //                for (int i = 0; i < geom.getNumGeometries(); i++) {
            //                    var renderGeom =
            //                            context.renderablePolygonProvider()
            //                                    .getRenderable(((Polygon)
            // geom.getGeometryN(i)));
            //                    var xs = renderGeom.converted().get(0).xs();
            //                    var ys = renderGeom.converted().get(0).ys();
            //                    context.graphicsContext().fillPolygon(xs, ys, xs.length);
            //                    fillPolygon(context, (Polygon) geom.getGeometryN(i));
            //                    fillPolygon(
            //                            context,
            //                            context.renderablePolygonProvider()
            //                                    .getRenderable(geom.getGeometryN(i)));

            //    }
//    private static void fillLineString(RenderingContext<?> context, LineString lineString) {
//        var tx = context.worldToScreen();
//        tx.copyCoordinatesG(lineString);
//        context.graphicsContext().fillPolygon(tx.getXs(), tx.getYs(), tx.getXs().length);
//    }
//
//    private static void fillPolygon(RenderingContext<?> context, Polygon polygon) {
//
//        // TODO -- sort this out -- finding stuff not in the bounding box
//        //    if(!polygon.intersects(JTS.toGeometry(context.bounds())))
//        //    {
//        ////      System.err.println("DUMB POLY");
//        //      return;
//        //    }
//
//        var geom = RemoveHolesFromPolygon.removePolygonG(polygon);
//        var tx = context.worldToScreen();
//        tx.copyCoordinatesG((Polygon) geom);
//        context.graphicsContext().fillPolygon(tx.getXs(), tx.getYs(), tx.getXs().length);
//    }
    // only necessary if a multi-X, can contain another multi-X, rather than just X's
    // private static void recurse(GraphicsRenderingContext context, Geometry geom) {
    // for (int i = 0; i < geom.getNumGeometries(); i++) {
    // fill(context, geom.getGeometryN(i));
    // }
    // }
//
//
//  // TODO -- switch on strings is brain dead
//        switch (geom.getGeometryType()) {
//            case Geometry.TYPENAME_POINT -> fillPoint(context, (Point) geom);
//            case Geometry.TYPENAME_LINESTRING, Geometry.TYPENAME_LINEARRING ->
//                    fillLineString(context, (LineString) geom);
//            case Geometry.TYPENAME_POLYGON -> fillPolygon(context, (Polygon) geom);
//            case Geometry.TYPENAME_MULTIPOINT -> {
//                for (int i = 0; i < geom.getNumGeometries(); i++) {
//                    fillPoint(context, (Point) geom.getGeometryN(i));
//                }
//            }
//            case Geometry.TYPENAME_MULTILINESTRING -> {
//                for (int i = 0; i < geom.getNumGeometries(); i++) {
//                    fillLineString(context, (LineString) geom.getGeometryN(i));
//                }
//            }
//            case Geometry.TYPENAME_MULTIPOLYGON -> {
//                for (int i = 0; i < geom.getNumGeometries(); i++) {
//                    fillPolygon(context, (Polygon) geom.getGeometryN(i));
//                }
//            }
//            default -> {
//                for (int i = 0; i < geom.getNumGeometries(); i++) {
//                    fill(context, geom.getGeometryN(i));
//                } // recurse(context, geom);
//            }
//        }
//    }
//
//    public static void fill(
//            GraphicsRenderingContext<?, ?> context,
//            Geometry geom,
//            FeatureGeomType featureGeomType) {
//
//        switch (featureGeomType) {
//            case POINT -> fillPoint(context, (Point) geom);
//            case LINE_STRING, LINEAR_RING -> fillLineString(context, (LineString) geom);
//            case POLYGON -> fillPolygon(context, (Polygon) geom);
//            case MULTI_POINT -> {
//                for (int i = 0; i < geom.getNumGeometries(); i++) {
//                    fillPoint(context, (Point) geom.getGeometryN(i));
//                }
//            }
//            case MULTI_LINE_STRING -> {
//                for (int i = 0; i < geom.getNumGeometries(); i++) {
//                    fillLineString(context, (LineString) geom.getGeometryN(i));
//                }
//            }
//            case MULTI_POLYGON -> {
//                for (int i = 0; i < geom.getNumGeometries(); i++) {
//                    fillPolygon(context, (Polygon) geom.getGeometryN(i));
//                }
//            }
//            default -> {
//                // TODO -- recurses wrong, fix
//                for (int i = 0; i < geom.getNumGeometries(); i++) {
//                    fill(context, geom.getGeometryN(i));
//                }
//            }
//        }

    //
    //    public static void fill(
    //            RenderingContext<?> context, Geometry geom, FeatureGeomType featureGeomType) {
    //
    //        switch (featureGeomType) {
    //            case POINT -> fillPoint(context, (Point) geom);
    //            case LINE_STRING, LINEAR_RING -> fillLineString(context, (LineString) geom);
    //            case POLYGON -> fillPolygon(context, (Polygon) geom);
    //            case MULTI_POINT -> {
    //                for (int i = 0; i < geom.getNumGeometries(); i++) {
    //                    fillPoint(context, (Point) geom.getGeometryN(i));
    //                }
    //            }
    //            case MULTI_LINE_STRING -> {
    //                for (int i = 0; i < geom.getNumGeometries(); i++) {
    //                    fillLineString(context, (LineString) geom.getGeometryN(i));
    //                }
    //            }
    //            case MULTI_POLYGON -> {
    //                for (int i = 0; i < geom.getNumGeometries(); i++) {
    //                    fillPolygon(context, (Polygon) geom.getGeometryN(i));
    //                }
    //            }
    //            default -> {
    //                // TODO -- recurses wrong, fix
    //                for (int i = 0; i < geom.getNumGeometries(); i++) {
    //                    fill(context, geom.getGeometryN(i));
    //                }
    //            }
    //        }
    //    }
    //    private static void fillLineString(RenderingContext<?> context, Converted converted) {
    //        var xs = converted.xs();
    //        var ys = converted.ys();
    //        context.graphicsContext().fillPolygon(xs, ys, xs.length);
    //    }

    //    private static void fillLine(RenderingContext<?> context, RenderableGeometry
    // renderableGeom) {
    //        assert renderableGeom != null;
    //
    //        for (var converted : renderableGeom.forFill()) {
    //            var xs = converted.xs();
    //            var ys = converted.ys();
    //            context.graphicsContext().fillPolygon(xs, ys, xs.length);
    //        }
    //    }
//
//    private static void fillAsPolygon(
//            RenderingContext<?> context, Collection<RenderableShape> shapes) {
//
//        for (var shape : shapes) {
//            var xs = shape.xs();
//            var ys = shape.ys();
//            context.graphicsContext().fillPolygon(xs, ys, xs.length);
//        }
//    }