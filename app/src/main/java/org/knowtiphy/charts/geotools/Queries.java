/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.geotools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.memstore.MemStoreQuery;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.knowtiphy.shapemap.viewmodel.MapViewModel;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * @author graham
 */
public class Queries {

	private static final double DELTA = 0.1;

	public static List<SimpleFeatureCollection> featuresNearXYWorld(MapViewModel map, double x, double y)
			throws IOException {

		var tx = new Transformation(map.viewPortScreenToWorld());
		tx.apply(x, y);
		var envelope = new ReferencedEnvelope(tx.getX() - DELTA, tx.getX() + DELTA, tx.getY() - DELTA,
				tx.getY() + DELTA, map.crs());

		var result = new ArrayList<SimpleFeatureCollection>();
		for (var layer : map.layers()) {
			var query = new MemStoreQuery(envelope, true);
			result.add((SimpleFeatureCollection) layer.getFeatureSource().getFeatures(query));
		}

		return result;
	}

	public static List<SimpleFeatureCollection> featuresNearXYWorld(MapViewModel map, double x, double y, int radius)
			throws IOException {

		var envelope = tinyPolygon(map, x, y, radius);

		var result = new ArrayList<SimpleFeatureCollection>();
		var foo = new ArrayList<SimpleFeature>();
		for (var layer : map.layers()) {
			var query = new MemStoreQuery(envelope, true);
			result.add((SimpleFeatureCollection) layer.getFeatureSource().getFeatures(query));
			var it = layer.getFeatureSource().getFeatures(query).features();
			while (it.hasNext()) {
				foo.add(it.next());
			}
		}

		var tx = new Transformation(map.viewPortScreenToWorld());
		tx.apply(x, y);

		var pt = new GeometryFactory().createPoint(new Coordinate(tx.getX(), tx.getY()));
		for (var f : foo) {
			var geom = (Geometry) f.getDefaultGeometry();
			for (var i = 0; i < geom.getNumGeometries(); i++) {
				var g = geom.getGeometryN(i);
				if (g.contains(pt)) {
					System.err.println("Geom " + g + " : " + g.contains(pt));
				}
			}
		}

		return result;
	}

	public static ReferencedEnvelope tinyPolygon(MapViewModel map, double x, double y, int radius) {
		int screenMinX = (int) x - radius;
		int screenMinY = (int) y - radius;
		int screenMaxX = (int) x + radius;
		int screenMaxY = (int) y + radius;
		/*
		 * Transform the screen rectangle into bounding box in the coordinate reference
		 * system of our map context. Note: we are using a naive method here but GeoTools
		 * also offers other, more accurate methods.
		 */
		Transformation tx = new Transformation(map.viewPortScreenToWorld());
		tx.apply(screenMinX, screenMinY);
		double minX = tx.getX();
		double minY = tx.getY();
		tx.apply(screenMaxX, screenMaxY);
		double maxX = tx.getX();
		double maxY = tx.getY();
		double width = maxX - minX;
		double height = maxY - minY;
		// TODO -- fix this as upside down
		return new ReferencedEnvelope(minX, minX + width, minY, minY + height, map.crs());
	}

	public static ReferencedEnvelope tinyPolygon(MapViewModel map, double x, double y) {
		return tinyPolygon(map, x, y, 1);
	}

}