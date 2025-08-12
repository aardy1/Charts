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

        var renderableGeom = context.renderablePolygonProvider().getRenderableGeometry(feature);

        //  TODO -- seems kind of silly to cache points and multi-points -- indeed anything but
        // (multi) polys
        if (renderableGeom == null) {
            //  fallback to using the geometries directly
            stroke(context, context.featureAdapter().defaultGeometry(feature));
        } else {
            switch (context.featureAdapter().geomType(feature)) {
                case POINT, MULTI_POINT -> strokePoint(context, renderableGeom);
                case LINE_STRING, LINEAR_RING, MULTI_LINE_STRING, POLYGON, MULTI_POLYGON ->
                        strokeAsPolygon(context, renderableGeom);
                default ->
                        throw new IllegalArgumentException(
                                "Unknown geometry type "
                                        + context.featureAdapter().geomType(feature));
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

    // if we don't have a renderable geometry for a geometry then fall back to dynamically
    // converting geometries to renderable geometries as we go
    private static void stroke(RenderingContext<?> context, Geometry geom) {

        //  this is a switch on strings in disguise
        switch (context.featureAdapter().geomType(geom)) {
            case LINE_STRING, LINEAR_RING, MULTI_LINE_STRING ->
                    strokeAsPolygon(
                            context,
                            context.renderablePolygonProvider().getRenderableGeometry(geom));
            //  could do the next two with renderable geom but seems sort of pointless
            case POINT -> strokePoint(context, (Point) geom);
            case MULTI_POINT -> {
                for (var i = 0; i < geom.getNumGeometries(); i++) {
                    strokePoint(context, (Point) geom.getGeometryN(i));
                }
            }
            case POLYGON -> strokePolygon(context, (Polygon) geom);
            case MULTI_POLYGON -> {
                for (var i = 0; i < geom.getNumGeometries(); i++) {
                    strokePolygon(context, (Polygon) geom.getGeometryN(i));
                }
            }
            default ->
                    throw new IllegalArgumentException(
                            "Unknown geometry type " + context.featureAdapter().geomType(geom));
        }
    }

    private static void strokePoint(RenderingContext<?> context, Point point) {
        context.graphicsContext()
                .strokeOval(point.getX(), point.getY(), context.onePixelX(), context.onePixelY());
    }

    //  have to be careful with polys as you want to stroke their interior rings
    public static void strokePolygon(RenderingContext<?> context, Polygon polygon) {
        stroke(context, polygon.getBoundary());
        for (var i = 0; i < polygon.getNumInteriorRing(); i++) {
            stroke(context, polygon.getInteriorRingN(i));
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