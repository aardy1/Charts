/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.graphics;

import org.geotools.geometry.jts.JTS;
import shapemap.renderer.api.GeomType;
import shapemap.renderer.GraphicsRenderingContext;
import shapemap.renderer.symbolizer.basic.FillInfo;
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

	public static void setup(GraphicsRenderingContext context, FillInfo fillInfo) {
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

	public static void fill(GraphicsRenderingContext context, Geometry geom) {

		// TODO -- switch on strings is brain dead
		switch (geom.getGeometryType()) {
			case Geometry.TYPENAME_POINT -> fillPoint(context, (Point) geom);
			case Geometry.TYPENAME_LINESTRING, Geometry.TYPENAME_LINEARRING ->
				fillLineString(context, (LineString) geom);
			case Geometry.TYPENAME_POLYGON -> fillPolygon(context, (Polygon) geom);
			case Geometry.TYPENAME_MULTIPOINT -> {
				for (int i = 0; i < geom.getNumGeometries(); i++) {
					fillPoint(context, (Point) geom.getGeometryN(i));
				}
			}
			case Geometry.TYPENAME_MULTILINESTRING -> {
				for (int i = 0; i < geom.getNumGeometries(); i++) {
					fillLineString(context, (LineString) geom.getGeometryN(i));
				}
			}
			case Geometry.TYPENAME_MULTIPOLYGON -> {
				for (int i = 0; i < geom.getNumGeometries(); i++) {
					fillPolygon(context, (Polygon) geom.getGeometryN(i));
				}
			}
			default -> {
				for (int i = 0; i < geom.getNumGeometries(); i++) {
					fill(context, geom.getGeometryN(i));
				} // recurse(context, geom);
			}
		}
	}

	public static void fill(GraphicsRenderingContext context, Geometry geom, GeomType geomType) {

		switch (geomType) {
			case POINT -> fillPoint(context, (Point) geom);
			case LINE_STRING, LINEAR_RING -> fillLineString(context, (LineString) geom);
			case POLYGON -> fillPolygon(context, (Polygon) geom);
			case MULTI_POINT -> {
				for (int i = 0; i < geom.getNumGeometries(); i++) {
					fillPoint(context, (Point) geom.getGeometryN(i));
				}
			}
			case MULTI_LINE_STRING -> {
				for (int i = 0; i < geom.getNumGeometries(); i++) {
					fillLineString(context, (LineString) geom.getGeometryN(i));
				}
			}
			case MULTI_POLYGON -> {
				for (int i = 0; i < geom.getNumGeometries(); i++) {
					fillPolygon(context, (Polygon) geom.getGeometryN(i));
				}
			}
			default -> {
				// TODO -- recurses wrong, fix
				for (int i = 0; i < geom.getNumGeometries(); i++) {
					fill(context, geom.getGeometryN(i));
				}
			}
		}
	}

	private static void fillPoint(GraphicsRenderingContext context, Point point) {
		context.graphicsContext().fillRect(point.getX(), point.getY(), context.onePixelX(), context.onePixelY());
	}

	private static void fillLineString(GraphicsRenderingContext context, LineString lineString) {
		var tx = context.worldToScreen();
		tx.copyCoordinatesG(lineString);
		context.graphicsContext().fillPolygon(tx.getXs(), tx.getYs(), tx.getXs().length);
	}

	private static void fillPolygon(GraphicsRenderingContext context, Polygon polygon) {

		// TODO -- sort this out -- finding stuff not in the bounding box
		if (!polygon.intersects(JTS.toGeometry(context.bounds()))) {
			System.err.println("DUMB POLY");
			return;
		}

		var tx = context.worldToScreen();
		var gc = context.graphicsContext();
		var renderGeom = context.rendererContext().renderablePolygonProvider().get(polygon);
		tx.copyCoordinatesG((Polygon) renderGeom);
		gc.fillPolygon(tx.getXs(), tx.getYs(), tx.getXs().length);
	}

	// only necessary if a multi-X, can contain another multi-X, rather than just X's
	// private static void recurse(GraphicsRenderingContext context, Geometry geom) {
	// for (int i = 0; i < geom.getNumGeometries(); i++) {
	// fill(context, geom.getGeometryN(i));
	// }
	// }

}
