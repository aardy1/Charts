/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.geotools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.api.IFeatureSourceIterator;
import org.knowtiphy.shapemap.api.model.MapViewModel;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * @author graham
 */
public class Queries {

	private static final double DELTA = 0.1;

	public static <S, F extends IFeature> List<SimpleFeatureCollection> featuresNearXYWorld(MapViewModel<S, F> map,
			double x, double y) throws IOException {

		var tx = new Transformation(map.viewPortScreenToWorld());
		tx.apply(x, y);
		var envelope = new ReferencedEnvelope(tx.getX() - DELTA, tx.getX() + DELTA, tx.getY() - DELTA,
				tx.getY() + DELTA, map.crs());

		var result = new ArrayList<SimpleFeatureCollection>();
		for (var layer : map.layers()) {
			result.add((SimpleFeatureCollection) layer.getFeatures(envelope, true));
		}

		return result;
	}

	public static <S, F extends IFeature> List<IFeatureSourceIterator<S, F>> featuresNearXYWorld(MapViewModel<S, F> map,
			double x, double y, int radius) throws Exception {

		var envelope = tinyPolygon(map, x, y, radius);

		var result = new ArrayList<IFeatureSourceIterator<S, F>>();
		var foo = new ArrayList<F>();
		for (var layer : map.layers()) {
			result.add(layer.getFeatureSource().features(envelope, true));
			var it = layer.getFeatureSource().features(envelope, true);
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