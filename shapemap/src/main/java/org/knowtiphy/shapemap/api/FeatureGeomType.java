/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

/**
 * A feature geometry type -- used for fast switching on geometry types (rather than using string
 * names and hence compares for geometry types).
 */

public enum FeatureGeomType
{
    // @formatter:off
    POINT,
    MULTI_POINT,
    LINE_STRING,
    LINEAR_RING,
    MULTI_LINE_STRING,
    POLYGON,
    MULTI_POLYGON,
    GEOMETRY_COLLECTION
    // @formatter:on
}