/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.graphics;

import static org.knowtiphy.shapemap.api.FeatureGeomType.LINEAR_RING;
import static org.knowtiphy.shapemap.api.FeatureGeomType.LINE_STRING;
import static org.knowtiphy.shapemap.api.FeatureGeomType.MULTI_LINE_STRING;
import static org.knowtiphy.shapemap.api.FeatureGeomType.MULTI_POLYGON;
import static org.knowtiphy.shapemap.api.FeatureGeomType.POINT;
import static org.knowtiphy.shapemap.api.FeatureGeomType.POLYGON;
import org.knowtiphy.shapemap.api.IRenderableGeometry;
import org.knowtiphy.shapemap.renderer.RenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

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
     * Called to fill a feature's geometry.
     *
     * @param context the rendering context
     * @param feature the feature to render
     * @param <F> the type of the feature
     */
    public static <F> void fill(RenderingContext<F> context, F feature) {

        var renderableGeometry = context.renderablePolygonProvider().getRenderableGeometry(feature);

        if (renderableGeometry == null) {
            //  fallback to using the geometries directly
            fill(context, context.featureAdapter().defaultGeometry(feature));
        } else {
            switch (context.featureAdapter().geometryType(feature)) {
                case POINT, MULTI_POINT -> fillPoint(context, renderableGeometry);
                case LINE_STRING, LINEAR_RING, POLYGON, MULTI_LINE_STRING, MULTI_POLYGON ->
                        fillAsPolygon(context, renderableGeometry);
                // TODO -- support for geom collections
                default ->
                        throw new IllegalArgumentException(
                                "Unsupported geometry type "
                                        + context.featureAdapter().geometryType(feature));
            }
        }
    }

    private static void fillPoint(
            RenderingContext<?> context, IRenderableGeometry renderableGeometry) {
        assert renderableGeometry != null;

        for (var shape : renderableGeometry.forFill()) {
            var x = shape.xs()[0];
            var y = shape.ys()[0];
            context.graphicsContext().fillRect(x, y, context.onePixelX(), context.onePixelY());
        }
    }

    private static void fillAsPolygon(
            RenderingContext<?> context, IRenderableGeometry renderableGeometry) {
        assert renderableGeometry != null;

        for (var shape : renderableGeometry.forFill()) {
            var xs = shape.xs();
            var ys = shape.ys();
            context.graphicsContext().fillPolygon(xs, ys, xs.length);
        }
    }

    // if we don't have a renderable geometry for a feature then fall back to dynamically
    // converting geometries to renderable geometries as we go
    private static void fill(RenderingContext<?> context, Geometry geometry) {

        //  this is a switch on strings in disguise in the geomType() call
        switch (context.featureAdapter().geometryType(geometry)) {
            case POINT -> fillPoint(context, (Point) geometry);
            case LINE_STRING, LINEAR_RING -> fillLineString(context, (LineString) geometry);
            case POLYGON -> fillPolygon(context, (Polygon) geometry);
            case MULTI_POINT -> fillMultiPoint(context, (MultiPoint) geometry);
            case MULTI_LINE_STRING -> fillMultLineString(context, (MultiLineString) geometry);
            case MULTI_POLYGON -> fillMultiPolygon(context, (MultiPolygon) geometry);
            // TODO -- support for geom collections
            default ->
                    throw new IllegalArgumentException(
                            "Unknown geometry type "
                                    + context.featureAdapter().geometryType(geometry));
        }
    }

    private static void fillPoint(RenderingContext<?> context, Point point) {

        var renderableGeometry = context.renderablePolygonProvider().getRenderableGeometry(point);

        if (renderableGeometry == null) {
            context.graphicsContext()
                    .fillRect(point.getX(), point.getY(), context.onePixelX(), context.onePixelY());
        } else {
            fillPoint(context, renderableGeometry);
        }
    }

    private static void fillLineString(RenderingContext<?> context, LineString lineString) {

        var renderableGeometry =
                context.renderablePolygonProvider().getRenderableGeometry(lineString);

        if (renderableGeometry == null) {
            var tx = context.worldToScreen();
            tx.copyCoordinates(lineString);
            context.graphicsContext().fillPolygon(tx.getXs(), tx.getYs(), tx.getXs().length);
        } else {
            fillAsPolygon(context, renderableGeometry);
        }
    }

    private static void fillPolygon(RenderingContext<?> context, Polygon polygon) {

        var renderableGeometry = context.renderablePolygonProvider().getRenderableGeometry(polygon);

        if (renderableGeometry == null) {
            //  removing holes in polygons would be way too slow
            throw new IllegalArgumentException("Expected a renderable geometry for polyon");
        } else {
            fillAsPolygon(context, renderableGeometry);
        }
    }

    private static void fillMultiPoint(RenderingContext<?> context, MultiPoint multiPoint) {

        var renderableGeometry =
                context.renderablePolygonProvider().getRenderableGeometry(multiPoint);

        if (renderableGeometry == null) {
            for (int i = 0; i < multiPoint.getNumGeometries(); i++) {
                fillPoint(context, (Point) multiPoint.getGeometryN(i));
            }
        } else {
            fillPoint(context, renderableGeometry);
        }
    }

    private static void fillMultLineString(
            RenderingContext<?> context, MultiLineString multiLineString) {

        var renderableGeometry =
                context.renderablePolygonProvider().getRenderableGeometry(multiLineString);

        if (renderableGeometry == null) {
            for (int i = 0; i < multiLineString.getNumGeometries(); i++) {
                fillLineString(context, (LineString) multiLineString.getGeometryN(i));
            }
        } else {
            fillAsPolygon(context, renderableGeometry);
        }
    }

    private static void fillMultiPolygon(RenderingContext<?> context, MultiPolygon polygon) {

        var renderableGeometry = context.renderablePolygonProvider().getRenderableGeometry(polygon);

        if (renderableGeometry == null) {
            for (int i = 0; i < polygon.getNumGeometries(); i++) {
                fillPolygon(context, (Polygon) polygon.getGeometryN(i));
            }
        } else {
            fillAsPolygon(context, renderableGeometry);
        }
    }
}