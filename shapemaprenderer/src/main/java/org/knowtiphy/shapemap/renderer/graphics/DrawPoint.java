/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.graphics;

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
     * Called by a point symbolizer to compute a point for various geometries.
     *
     * <p>Note: point symbolizers can be used on any geometry.
     *
     * @param geom the geometry to render
     * @return the chosen point
     */
    public static <F> Point choosePoint(RenderingContext<F> context, Geometry geom) {

        //  this is a switch on strings in disguise
        return switch (context.featureAdapter().geomType(geom)) {
            case POINT -> (Point) geom;
            case LINE_STRING, LINEAR_RING -> ((LineString) geom).getStartPoint();
            case POLYGON, MULTI_POLYGON -> geom.getCentroid();
            default -> throw new IllegalArgumentException(geom.getGeometryType());
        };
    }
}