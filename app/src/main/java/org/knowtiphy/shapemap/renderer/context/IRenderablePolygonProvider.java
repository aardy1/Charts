/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.context;

import org.locationtech.jts.geom.Polygon;

/**
 * @author graham
 */
public interface IRenderablePolygonProvider {

	Polygon get(Polygon polygon);

}
