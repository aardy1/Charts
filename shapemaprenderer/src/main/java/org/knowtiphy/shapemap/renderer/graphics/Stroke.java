/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.graphics;

import org.knowtiphy.shapemap.api.FeatureGeomType;
import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public class Stroke {

    /**
     * Setup the stroke values for a graphics context from stroke information.
     *
     * @param context the rendering context
     * @param strokeInfo the stroke information
     */
    public static <S, F> void setup(
            GraphicsRenderingContext<S, F, ?> context, StrokeInfo strokeInfo) {
        var gc = context.graphicsContext();
        gc.setStroke(strokeInfo.stroke());
        // TODO -- hmm?
        gc.setLineWidth(strokeInfo.strokeWidth() * context.onePixelX());
        gc.setGlobalAlpha(strokeInfo.opacity());
    }

    /**
     * Called by a line and polygon symbolizer to stroke geometries.
     *
     * <p>Note: symbolizers can be used on any geometry.
     *
     * @param context the rendering context
     * @param geom the geometry to render
     */
    public static <S, F> void stroke(GraphicsRenderingContext<S, F, ?> context, Geometry geom) {

        // TODO -- switch on strings is brain dead
        switch (geom.getGeometryType()) {
            case Geometry.TYPENAME_POINT -> strokePoint(context, (Point) geom);
            case Geometry.TYPENAME_LINESTRING, Geometry.TYPENAME_LINEARRING ->
                    strokeLineString(context, (LineString) geom);
            case Geometry.TYPENAME_POLYGON -> strokePolygon(context, (Polygon) geom);
            case Geometry.TYPENAME_MULTIPOINT -> {
                for (var i = 0; i < geom.getNumGeometries(); i++) {
                    strokePoint(context, (Point) geom.getGeometryN(i));
                }
            }
            case Geometry.TYPENAME_MULTILINESTRING -> {
                for (var i = 0; i < geom.getNumGeometries(); i++) {
                    strokeLineString(context, (LineString) geom.getGeometryN(i));
                }
            }
            case Geometry.TYPENAME_MULTIPOLYGON -> {
                for (var i = 0; i < geom.getNumGeometries(); i++) {
                    strokePolygon(context, (Polygon) geom.getGeometryN(i));
                }
            }
            default -> {
                for (var i = 0; i < geom.getNumGeometries(); i++) {
                    stroke(context, geom.getGeometryN(i));
                }
            }
        }
    }

    public static <S, F> void stroke(
            GraphicsRenderingContext<S, F, ?> context,
            Geometry geom,
            FeatureGeomType featureGeomType) {

        // TODO -- switch on strings is brain dead
        switch (featureGeomType) {
            case POINT -> strokePoint(context, (Point) geom);
            case LINE_STRING, LINEAR_RING -> strokeLineString(context, (LineString) geom);
            case POLYGON -> strokePolygon(context, (Polygon) geom);
            case MULTI_POINT -> {
                for (var i = 0; i < geom.getNumGeometries(); i++) {
                    strokePoint(context, (Point) geom.getGeometryN(i));
                }
            }
            case MULTI_LINE_STRING -> {
                for (var i = 0; i < geom.getNumGeometries(); i++) {
                    strokeLineString(context, (LineString) geom.getGeometryN(i));
                }
            }
            case MULTI_POLYGON -> {
                for (var i = 0; i < geom.getNumGeometries(); i++) {
                    strokePolygon(context, (Polygon) geom.getGeometryN(i));
                }
            }
            default -> {
                // TODO -- this is wrong, fix
                for (var i = 0; i < geom.getNumGeometries(); i++) {
                    stroke(context, geom.getGeometryN(i));
                }
            }
        }
    }

    private static <S, F> void strokePoint(GraphicsRenderingContext<S, F, ?> context, Point point) {
        context.graphicsContext()
                .strokeOval(point.getX(), point.getY(), context.onePixelX(), context.onePixelY());
    }

    // line width calculated from pixels per world x-coordinate
    private static <S, F> void strokeLineString(
            GraphicsRenderingContext<S, F, ?> context, LineString lineString) {
        var tx = context.worldToScreen();
        tx.copyCoordinatesG(lineString);
        context.graphicsContext().strokePolyline(tx.getXs(), tx.getYs(), tx.getXs().length);
    }

    // if we are scaling in world coordinates it is faster to use the lineString() code --
    // need to know that in our styles
    private static <S, F> void strokeLineStringSVG(
            GraphicsRenderingContext<S, F, ?> context, LineString lineString) {
        var gc = context.graphicsContext();

        gc.beginPath();

        var start = lineString.getCoordinateN(0);
        gc.moveTo(start.getX(), start.getY());

        for (var i = 1; i < lineString.getNumPoints(); i++) {
            var pt = lineString.getCoordinateN(i);
            gc.lineTo(pt.getX(), pt.getY());
        }
        var foo = gc.getTransform();
        gc.setTransform(Transformation.IDENTITY);
        // TODO -- fix this
        gc.setLineWidth(1);
        gc.stroke();
        gc.setTransform(foo);
    }

    public static <S, F> void strokePolygon(
            GraphicsRenderingContext<S, F, ?> context, Polygon polygon) {
        stroke(context, polygon.getBoundary());
        for (var i = 0; i < polygon.getNumInteriorRing(); i++) {
            stroke(context, polygon.getInteriorRingN(i));
        }
    }

    // this is only necessary because I am not sure if a multi-X, can contain another
    // multi-X, or just X's
    // private static void recurse(GraphicsRenderingContext context, Geometry geom) {
    // for (var i = 0; i < geom.getNumGeometries(); i++) {
    // stroke(context, geom.getGeometryN(i));
    // }
    // }

}