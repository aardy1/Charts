/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import org.locationtech.jts.geom.Geometry;

/**
 * A feature in an ESRI shape map.
 */
public interface IFeatureAdapter<F> {

	Geometry defaultGeometry(F feature);

	GeomType geomType(F feature);

}
