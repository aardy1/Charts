/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import org.locationtech.jts.geom.Geometry;

/**
 * An adapter to extract geometry information from a feature of type F. <param> F the type of the
 * feature.
 *
 * <p>This allows geometry extraction without requiring that F implements some interface or
 * subclasses some class (makes for flexibility at the cost of a function call)
 */
public interface IFeatureAdapter<F> {
    Geometry defaultGeometry(F feature);

    FeatureGeomType geomType(F feature);

    FeatureGeomType geomType(Geometry geom);
}