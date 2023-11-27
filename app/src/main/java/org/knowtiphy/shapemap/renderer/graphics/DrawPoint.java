/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.graphics;

import org.knowtiphy.shapemap.renderer.RenderingContext;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public class DrawPoint {

	/**
	 * Setup the stroke values for a graphics context from stroke information.
	 * @param context the rendering context
	 * @param opacity the opacity for the points
	 */

	public static void setup(RenderingContext context, double opacity) {
		context.graphicsContext().setGlobalAlpha(opacity);
	}

	/**
	 * Called by a point symbolizer to compute a point for various geometries.
	 *
	 * Note: point symbolizers can be used on any geometry.
	 * @param geom the geometry to render
	 * @return the chosen point
	 */

	public static Point choosePoint(Geometry geom) {

		return switch (geom.getGeometryType()) {
			case Geometry.TYPENAME_POINT -> (Point) geom;
			case Geometry.TYPENAME_LINESTRING, Geometry.TYPENAME_LINEARRING -> ((LineString) geom).getStartPoint();
			case Geometry.TYPENAME_POLYGON, Geometry.TYPENAME_MULTIPOLYGON -> ((Polygon) geom).getCentroid();
			default -> throw new IllegalArgumentException(geom.getGeometryType());
		};
	}

}