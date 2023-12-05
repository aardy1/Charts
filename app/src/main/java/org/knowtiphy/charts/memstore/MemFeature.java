/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.util.List;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.filter.identity.FeatureId;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.knowtiphy.shapemap.renderer.api.GeomType;
import org.knowtiphy.shapemap.renderer.api.IFeature;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class MemFeature extends SimpleFeatureImpl implements IFeature {

	private final GeomType geomType;

	public MemFeature(List<Object> values, SimpleFeatureType featureType, FeatureId id, GeomType geomType) {
		super(values, featureType, id);
		this.geomType = geomType;
	}

	public GeomType geomType() {
		return geomType;
	}

	@Override
	public Geometry getDefaultGeometry() {
		return (Geometry) super.getDefaultGeometry();
	}

}
