/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.graphics;

import static org.knowtiphy.shapemap.api.FeatureGeomType.LINEAR_RING;
import static org.knowtiphy.shapemap.api.FeatureGeomType.LINE_STRING;
import static org.knowtiphy.shapemap.api.FeatureGeomType.MULTI_LINE_STRING;
import static org.knowtiphy.shapemap.api.FeatureGeomType.MULTI_POINT;
import static org.knowtiphy.shapemap.api.FeatureGeomType.MULTI_POLYGON;
import static org.knowtiphy.shapemap.api.FeatureGeomType.POINT;
import static org.knowtiphy.shapemap.api.FeatureGeomType.POLYGON;
import org.knowtiphy.shapemap.api.RenderableGeometry;
import org.knowtiphy.shapemap.renderer.RenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public class Stroke {

    /**
     * Setup the stroke values for a graphics context from stroke information.
     *
     * @param context the rendering context
     * @param strokeInfo the stroke information
     */
    public static void setup(RenderingContext<?> context, StrokeInfo strokeInfo) {
        var gc = context.graphicsContext();
        gc.setStroke(strokeInfo.stroke());
        // TODO -- hmm?
        gc.setLineWidth(strokeInfo.strokeWidth() * context.onePixelX());
        gc.setGlobalAlpha(strokeInfo.opacity());
    }

    /**
     * Called by a line or polygon symbolizer to stroke a feature's geometry. Note that line and
     * polygon symbolizers can be used on any geometry.
     *
     * @param <F> the type of the feature
     * @param context the rendering context
     * @param feature the feature to render
     */
    public static <F> void stroke(RenderingContext<F> context, F feature) {

        var renderableGeometry = context.renderablePolygonProvider().getRenderableGeometry(feature);

        if (renderableGeometry == null) {
            //  fallback to using the geometries directly
            stroke(context, context.featureAdapter().defaultGeometry(feature));
        } else {
            switch (context.featureAdapter().geometryType(feature)) {
                case POINT, MULTI_POINT -> strokePoint(context, renderableGeometry);
                case LINE_STRING, LINEAR_RING, POLYGON, MULTI_LINE_STRING, MULTI_POLYGON ->
                        strokeAsPolygon(context, renderableGeometry);
                // TODO -- support for geom collections
                default ->
                        throw new IllegalArgumentException(
                                "Unsupported geometry type "
                                        + context.featureAdapter().geometryType(feature));
            }
        }
    }

    private static void strokePoint(
            RenderingContext<?> context, RenderableGeometry renderableGeometry) {
        assert renderableGeometry != null;

        for (var shape : renderableGeometry.forStroke()) {
            var x = shape.xs()[0];
            var y = shape.ys()[0];
            context.graphicsContext().strokeOval(x, y, context.onePixelX(), context.onePixelY());
        }
    }

    private static void strokeAsPolygon(
            RenderingContext<?> context, RenderableGeometry renderableGeometry) {
        assert renderableGeometry != null;

        for (var shape : renderableGeometry.forStroke()) {
            var xs = shape.xs();
            var ys = shape.ys();
            context.graphicsContext().strokePolyline(xs, ys, xs.length);
        }
    }

    // if we don't have a renderable geometry for a feature then fall back to dynamically
    // converting geometries to renderable geometries as we go
    private static void stroke(RenderingContext<?> context, Geometry geometry) {

        //  this is a switch on strings in disguise in the geomType() call
        switch (context.featureAdapter().geometryType(geometry)) {
            case POINT -> strokePoint(context, (Point) geometry);
            case LINE_STRING, LINEAR_RING -> strokeLineString(context, (LineString) geometry);
            case POLYGON -> strokePolygon(context, (Polygon) geometry);
            case MULTI_POINT -> strokeMultiPoint(context, (MultiPoint) geometry);
            case MULTI_LINE_STRING -> strokeMultiLineString(context, (MultiLineString) geometry);
            case MULTI_POLYGON -> strokeMultiPolygon(context, (MultiPolygon) geometry);
            default ->
                    throw new IllegalArgumentException(
                            "Unknown geometry type "
                                    + context.featureAdapter().geometryType(geometry));
        }
    }

    private static void strokePoint(RenderingContext<?> context, Point point) {

        var renderableGeometry = context.renderablePolygonProvider().getRenderableGeometry(point);

        if (renderableGeometry == null) {
            context.graphicsContext()
                    .strokeOval(
                            point.getX(), point.getY(), context.onePixelX(), context.onePixelY());
        } else {
            strokePoint(context, renderableGeometry);
        }
    }

    private static void strokeLineString(RenderingContext<?> context, LineString lineString) {

        var renderableGeometry =
                context.renderablePolygonProvider().getRenderableGeometry(lineString);

        if (renderableGeometry == null) {
            var tx = context.worldToScreen();
            tx.copyCoordinates(lineString);
            context.graphicsContext().strokePolyline(tx.getXs(), tx.getYs(), tx.getXs().length);
        } else {
            strokeAsPolygon(context, renderableGeometry);
        }
    }

    //  have to be careful with polys as you want to stroke their interior rings
    public static void strokePolygon(RenderingContext<?> context, Polygon polygon) {

        var renderableGeometry = context.renderablePolygonProvider().getRenderableGeometry(polygon);

        if (renderableGeometry == null) {
            //  removing holes in polygons would be way too slow
            throw new IllegalArgumentException("Expected a renderable geometry for polyon");
        } else {
            strokeAsPolygon(context, renderableGeometry);
        }
        //        stroke(context, polygon.getBoundary());
        //        for (var i = 0; i < polygon.getNumInteriorRing(); i++) {
        //            strokeLineString(context, polygon.getInteriorRingN(i));
        //        }
    }

    private static void strokeMultiPoint(RenderingContext<?> context, MultiPoint multiPoint) {

        var renderableGeometry =
                context.renderablePolygonProvider().getRenderableGeometry(multiPoint);

        if (renderableGeometry == null) {
            for (int i = 0; i < multiPoint.getNumGeometries(); i++) {
                strokePoint(context, (Point) multiPoint.getGeometryN(i));
            }
        } else {
            strokePoint(context, renderableGeometry);
        }
    }

    private static void strokeMultiLineString(
            RenderingContext<?> context, MultiLineString multiLineString) {

        var renderableGeometry =
                context.renderablePolygonProvider().getRenderableGeometry(multiLineString);

        if (renderableGeometry == null) {
            for (int i = 0; i < multiLineString.getNumGeometries(); i++) {
                strokeLineString(context, (LineString) multiLineString.getGeometryN(i));
            }
        } else {
            strokeAsPolygon(context, renderableGeometry);
        }
    }

    private static void strokeMultiPolygon(RenderingContext<?> context, MultiPolygon polygon) {

        var renderableGeometry = context.renderablePolygonProvider().getRenderableGeometry(polygon);

        if (renderableGeometry == null) {
            for (int i = 0; i < polygon.getNumGeometries(); i++) {
                strokePolygon(context, (Polygon) polygon.getGeometryN(i));
            }
        } else {
            strokeAsPolygon(context, renderableGeometry);
        }
    }
}

            //            case MULTI_LINE_STRING -> {
            //                //                for (var i = 0; i < geom.getNumGeometries(); i++) {
            //                strokeAsPolygon(
            //                        context,
            // context.renderablePolygonProvider().getRenderableGeometry(geom));
            //                //
            // .getRenderableGeometry(geom.getGeometryN(i)));
            //                //                }
            //        }

    // if we are scaling in world coordinates it is faster to use the lineString() code --
    // need to know that in our styles
//    private static void strokeLineStringSVG(RenderingContext<?> context, LineString lineString) {
//        var gc = context.graphicsContext();
//
//        gc.beginPath();
//
//        var start = lineString.getCoordinateN(0);
//        gc.moveTo(start.getX(), start.getY());
//
//        for (var i = 1; i < lineString.getNumPoints(); i++) {
//            var pt = lineString.getCoordinateN(i);
//            gc.lineTo(pt.getX(), pt.getY());
//        }
//        var foo = gc.getTransform();
//        //        gc.setTransform(Transformation.IDENTITY);
//        // TODO -- fix this
//        gc.setLineWidth(1);
//        gc.stroke();
//        gc.setTransform(foo);
//    }

    // this is only necessary because I am not sure if a multi-X, can contain another
    // multi-X, or just X's
    // private static void recurse(GraphicsRenderingContext context, Geometry geom) {
    // for (var i = 0; i < geom.getNumGeometries(); i++) {
    // stroke(context, geom.getGeometryN(i));
    // }
    // }
//
//    public static void stroke(
//            RenderingContext<?> context, Geometry geom, FeatureGeomType featureGeomType) {
//
//        // TODO -- switch on strings is brain dead
//        switch (featureGeomType) {
//            case POINT -> strokePoint(context, (Point) geom);
//            case LINE_STRING, LINEAR_RING -> strokeLineString(context, (LineString) geom);
//            case POLYGON -> strokePolygon(context, (Polygon) geom);
//            case MULTI_POINT -> {
//                for (var i = 0; i < geom.getNumGeometries(); i++) {
//                    strokePoint(context, (Point) geom.getGeometryN(i));
//                }
//            }
//            case MULTI_LINE_STRING -> {
//                for (var i = 0; i < geom.getNumGeometries(); i++) {
//                    strokeLineString(context, (LineString) geom.getGeometryN(i));
//                }
//            }
//            case MULTI_POLYGON -> {
//                for (var i = 0; i < geom.getNumGeometries(); i++) {
//                    strokePolygon(context, (Polygon) geom.getGeometryN(i));
//                }
//            }
//            default -> {
//                // TODO -- this is wrong, fix
//                for (var i = 0; i < geom.getNumGeometries(); i++) {
//                    stroke(context, geom.getGeometryN(i));
//                }
//            }
//        }
//    }