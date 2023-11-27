/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style;

import org.geotools.api.feature.Feature;
import org.knowtiphy.charts.memstore.ExtraAttributes;
import org.knowtiphy.charts.memstore.GeomType;

/**
 * @author graham
 */
public class Utils {

	public static GeomType getGeomType(Feature feature) {
		return ExtraAttributes.geomType(feature);
	}

}
