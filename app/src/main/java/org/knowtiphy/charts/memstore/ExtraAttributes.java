/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import org.knowtiphy.shapemap.renderer.api.GeomType;
import org.geotools.api.feature.Feature;
import org.geotools.api.feature.simple.SimpleFeature;
import org.locationtech.jts.geom.Geometry;

import static org.locationtech.jts.geom.Geometry.TYPENAME_GEOMETRYCOLLECTION;
import static org.locationtech.jts.geom.Geometry.TYPENAME_LINEARRING;
import static org.locationtech.jts.geom.Geometry.TYPENAME_LINESTRING;
import static org.locationtech.jts.geom.Geometry.TYPENAME_MULTILINESTRING;
import static org.locationtech.jts.geom.Geometry.TYPENAME_MULTIPOINT;
import static org.locationtech.jts.geom.Geometry.TYPENAME_MULTIPOLYGON;
import static org.locationtech.jts.geom.Geometry.TYPENAME_POINT;
import static org.locationtech.jts.geom.Geometry.TYPENAME_POLYGON;

/**
 * @author graham
 */
public class ExtraAttributes {

	public static final String GEOM_TYPE = "geomType";

	public static void setGeomType(SimpleFeature feature) {

		var geomType = ((Geometry) feature.getDefaultGeometryProperty().getValue()).getGeometryType();
		var gt = switch (geomType) {
			case TYPENAME_POINT -> GeomType.POINT;
			case TYPENAME_MULTIPOINT -> GeomType.MULTI_POINT;
			case TYPENAME_LINESTRING -> GeomType.LINE_STRING;
			case TYPENAME_LINEARRING -> GeomType.LINEAR_RING;
			case TYPENAME_MULTILINESTRING -> GeomType.MULTI_LINE_STRING;
			case TYPENAME_POLYGON -> GeomType.POLYGON;
			case TYPENAME_MULTIPOLYGON -> GeomType.MULTI_POLYGON;
			case TYPENAME_GEOMETRYCOLLECTION -> GeomType.GEOMETRY_COLLECTION;
			// linear rings?
			default -> throw new IllegalArgumentException(geomType);
		};

		feature.getUserData().put(GEOM_TYPE, gt);
	}

	public static GeomType geomType(Feature feature) {
		return (GeomType) feature.getUserData().get(GEOM_TYPE);
	}

	public static GeomType setGeomType(Geometry geom) {

		return switch (geom.getGeometryType()) {
			case TYPENAME_POINT -> GeomType.POINT;
			case TYPENAME_MULTIPOINT -> GeomType.MULTI_POINT;
			case TYPENAME_LINESTRING -> GeomType.LINE_STRING;
			case TYPENAME_LINEARRING -> GeomType.LINEAR_RING;
			case TYPENAME_MULTILINESTRING -> GeomType.MULTI_LINE_STRING;
			case TYPENAME_POLYGON -> GeomType.POLYGON;
			case TYPENAME_MULTIPOLYGON -> GeomType.MULTI_POLYGON;
			case TYPENAME_GEOMETRYCOLLECTION -> GeomType.GEOMETRY_COLLECTION;
			default -> throw new IllegalArgumentException(geom.getGeometryType());
		};
	}

}
