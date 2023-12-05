/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public interface IFeature {

	Geometry getDefaultGeometry();

	GeomType geomType();

}
