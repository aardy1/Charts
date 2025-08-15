/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import org.locationtech.jts.geom.Geometry;

/** A provider of polygons that can be rendered (have no holes). */
public interface IRenderablePolygonProvider<F> {

    IRenderableGeometry getRenderableGeometry(Geometry geometry);

    IRenderableGeometry getRenderableGeometry(F feature);
}
