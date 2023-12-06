/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import java.util.function.Function;
import org.locationtech.jts.geom.Polygon;

/**
 * A provider of polygons that can be rendered (have no holes).
 */

public interface IRenderablePolygonProvider extends Function<Polygon, Polygon> {

}
