/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.graphics;

import static org.knowtiphy.shapemap.api.FeatureGeomType.MULTI_LINE_STRING;
import static org.knowtiphy.shapemap.api.FeatureGeomType.MULTI_POINT;
import static org.knowtiphy.shapemap.api.FeatureGeomType.MULTI_POLYGON;
import org.knowtiphy.shapemap.renderer.RenderingContext;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

public class DrawPoint {

    /**
     * Setup the stroke values for a graphics context from stroke information.
     *
     * @param context the rendering context
     * @param opacity the opacity for the points
     */
    public static void setup(RenderingContext<?> context, double opacity) {
        context.graphicsContext().setGlobalAlpha(opacity);
    }

    /**
     * Called by a symbolizer to compute a point for various geometries.
     *
     * @param context the rendering context
     * @param geom the geometry
     * @return the point
     */
    public static Point choosePoint(RenderingContext<?> context, Geometry geom) {

        //  this is a switch on strings in disguise
        return switch (context.featureAdapter().geometryType(geom)) {
            case POINT -> (Point) geom;
            case LINE_STRING, LINEAR_RING -> ((LineString) geom).getStartPoint();
            case POLYGON, MULTI_POINT, MULTI_LINE_STRING, MULTI_POLYGON -> geom.getCentroid();
            // TODO geom collections?
            default -> throw new IllegalArgumentException(geom.getGeometryType());
        };
    }
}