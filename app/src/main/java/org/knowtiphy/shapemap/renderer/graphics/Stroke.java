/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.graphics;

import org.knowtiphy.shapemap.renderer.RenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public class Stroke {

	/**
	 * Setup the stroke values for a graphics context from stroke information.
	 * @param context the rendering context
	 * @param strokeInfo the stroke information
	 */

	public static void setup(RenderingContext context, StrokeInfo strokeInfo) {
		var gc = context.graphicsContext();
		gc.setStroke(strokeInfo.stroke());
		// TODO -- hmm?
		gc.setLineWidth(strokeInfo.strokeWidth() * context.onePixelX());
		gc.setGlobalAlpha(strokeInfo.opacity());
	}

	/**
	 * Called by a line and polygon symbolizer to stroke geometries.
	 *
	 * Note: symbolizers can be used on any geometry.
	 * @param context the rendering context
	 * @param geom the geometry to render
	 */

	public static void stroke(RenderingContext context, Geometry geom) {

		// TODO -- switch on strings is brain dead
		switch (geom.getGeometryType()) {
			case Geometry.TYPENAME_POINT -> strokePoint(context, (Point) geom);
			case Geometry.TYPENAME_LINESTRING -> strokeLineString(context, (LineString) geom);
			case Geometry.TYPENAME_LINEARRING -> strokeLineString(context, (LineString) geom);
			case Geometry.TYPENAME_POLYGON -> strokePolygon(context, (Polygon) geom);
			case Geometry.TYPENAME_MULTIPOINT, Geometry.TYPENAME_MULTILINESTRING, Geometry.TYPENAME_MULTIPOLYGON,
					Geometry.TYPENAME_GEOMETRYCOLLECTION ->
				recurse(context, geom);
			default -> throw new IllegalArgumentException(geom.getGeometryType());
		}
	}

	private static void strokePoint(RenderingContext context, Point point) {
		var tx = context.worldToScreen();
		tx.apply(point.getX(), point.getY());
		context.graphicsContext().strokeOval(tx.getX(), tx.getY(), context.onePixelX(), context.onePixelY());
	}

	private static void strokeLineString(RenderingContext context, LineString lineString) {
		var tx = context.worldToScreen();
		tx.transformCoordinates(lineString.getCoordinates());
		context.graphicsContext().strokePolyline(tx.getXs(), tx.getYs(), tx.getXs().length);
	}

	public static void strokePolygon(RenderingContext context, Polygon polygon) {
		stroke(context, polygon.getBoundary());
		for (var i = 0; i < polygon.getNumInteriorRing(); i++) {
			stroke(context, polygon.getInteriorRingN(i));
		}
	}

	// this is only necessary because I am not sure if a multi-X, can contain another
	// multi-X, or just X's
	private static void recurse(RenderingContext context, Geometry geom) {
		for (var i = 0; i < geom.getNumGeometries(); i++) {
			stroke(context, geom.getGeometryN(i));
		}
	}

}