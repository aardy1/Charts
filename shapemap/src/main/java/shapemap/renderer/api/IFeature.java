/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.api;

import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public interface IFeature {

	Geometry getDefaultGeometry();

	GeomType geomType();

}
