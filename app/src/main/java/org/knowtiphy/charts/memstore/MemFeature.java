/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.util.List;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.filter.identity.FeatureId;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.knowtiphy.shapemap.api.GeomType;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class MemFeature extends SimpleFeatureImpl {

	private final GeomType geomType;

	public MemFeature(List<Object> values, SimpleFeatureType featureType, FeatureId id, GeomType geomType) {
		super(values, featureType, id);
		this.geomType = geomType;
	}

	public GeomType geomType() {
		return geomType;
	}

	public Geometry defaultGeometry() {
		return (Geometry) super.getDefaultGeometry();
	}

}
