/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import org.locationtech.jts.geom.Polygon;

/**
 * @author graham
 */
public interface IRenderablePolygonProvider {

	Polygon get(Polygon polygon);

}
