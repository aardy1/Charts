/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import org.locationtech.jts.geom.*;

/**
 * An adapter for features so that they can be used with the map renderer without requiring
 * features to implement some interface or subclass some class (makes for flexibility at the cost
 * of a function call)
 */

public interface IFeatureAdapter<F>
{

  Geometry defaultGeometry(F feature);

  GeomType geomType(F feature);

}