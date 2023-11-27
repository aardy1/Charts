/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.graphics;

import org.geotools.geometry.jts.JTS;
import org.knowtiphy.shapemap.renderer.RemoveHolesFromPolygon;
import org.knowtiphy.shapemap.renderer.RenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public class Fill {

	/**
	 * Setup the fill values for a graphics context from fill information.
	 * @param context the rendering context
	 * @param fillInfo the fill information
	 */

	public static void setup(RenderingContext context, FillInfo fillInfo) {
		context.graphicsContext().setFill(fillInfo.fill());
		context.graphicsContext().setGlobalAlpha(fillInfo.opacity());
	}

	/**
	 * Called by a polygon symbolizer to fill various geometries.
	 *
	 * Note: polygon symbolizers can be used on any geometry.
	 * @param context the rendering context
	 * @param geom the geometry to render
	 */

	public static void fill(RenderingContext context, Geometry geom) {

		// TODO -- switch on strings is brain dead
		switch (geom.getGeometryType()) {
			case Geometry.TYPENAME_POINT -> fillPoint(context, (Point) geom);
			case Geometry.TYPENAME_LINESTRING, Geometry.TYPENAME_LINEARRING ->
				fillLineString(context, (LineString) geom);
			case Geometry.TYPENAME_POLYGON -> fillPolygon(context, (Polygon) geom);
			case Geometry.TYPENAME_MULTIPOINT, Geometry.TYPENAME_MULTILINESTRING, Geometry.TYPENAME_MULTIPOLYGON ->
				recurse(context, geom);
			default -> throw new IllegalArgumentException(geom.getGeometryType());
		}
	}

	private static void fillPoint(RenderingContext context, Point point) {
		var tx = context.worldToScreen();
		tx.apply(point.getX(), point.getY());
		context.graphicsContext().fillRect(tx.getX(), tx.getY(), context.onePixelX(), context.onePixelY());
	}

	private static void fillLineString(RenderingContext context, LineString lineString) {
		var tx = context.worldToScreen();
		tx.transformCoordinates(lineString.getCoordinates());
		context.graphicsContext().fillPolygon(tx.getXs(), tx.getYs(), tx.getXs().length);
	}

	private static void fillPolygon(RenderingContext context, Polygon polygon) {

		// TODO -- sort this out -- finding stuff not in the bounding box
		if (!polygon.intersects(JTS.toGeometry(context.bounds()))) {
			System.err.println("DUMB POLY");
			return;
		}

		var tx = context.worldToScreen();
		var gc = context.graphicsContext();
		var renderGeomCache = context.renderGeomCache();

		// TODO -- we could compute this at load time
		var renderGeom = renderGeomCache.fetch(polygon);
		if (renderGeom == null) {
			renderGeom = RemoveHolesFromPolygon.remove(polygon);
			renderGeomCache.cache(polygon, renderGeom);
		}

		tx.transformCoordinates(renderGeom.getCoordinates());
		gc.fillPolygon(tx.getXs(), tx.getYs(), tx.getXs().length);
	}

	// only necessary if a multi-X, can contain another multi-X, rather than just X's
	private static void recurse(RenderingContext context, Geometry geom) {
		for (int i = 0; i < geom.getNumGeometries(); i++) {
			fill(context, geom.getGeometryN(i));
		}
	}

}
