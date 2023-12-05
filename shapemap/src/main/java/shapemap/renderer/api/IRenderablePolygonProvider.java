/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.api;

import org.locationtech.jts.geom.Polygon;

/**
 * @author graham
 */
public interface IRenderablePolygonProvider {

	Polygon get(Polygon polygon);

}
