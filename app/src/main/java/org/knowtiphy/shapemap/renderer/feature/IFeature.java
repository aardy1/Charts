/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.feature;

import org.geotools.api.feature.simple.SimpleFeatureType;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public interface IFeature {

	Geometry getDefaultGeometry();

	SimpleFeatureType getType();

}
