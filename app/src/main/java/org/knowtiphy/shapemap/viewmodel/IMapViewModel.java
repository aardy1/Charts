/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.viewmodel;

import java.util.Collection;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * @author graham
 */
public interface IMapViewModel {

	Collection<MapLayer> layers();

	ReferencedEnvelope viewPortBounds();

	CoordinateReferenceSystem crs();

	int totalRuleCount();

}
