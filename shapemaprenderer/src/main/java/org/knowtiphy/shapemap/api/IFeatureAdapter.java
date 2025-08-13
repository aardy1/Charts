/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import org.locationtech.jts.geom.Geometry;

/**
 * An adapter to extract geometry information from a feature of type F. .
 *
 * <p>This allows geometry extraction without requiring that F implements some interface or
 * subclasses some class (makes for flexibility at the cost of a function call).
 *
 * @param <F> the type of the features
 */
public interface IFeatureAdapter<F> {

    Geometry defaultGeometry(F feature);

    FeatureGeomType geometryType(F feature);

    FeatureGeomType geometryType(Geometry geometry);
}