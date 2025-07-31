/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import org.locationtech.jts.geom.Geometry;

import java.util.function.BiFunction;

/**
 * A function from a feature of type F to a value of type T. e.g. when filtering T will be boolean,
 * when styling T will be colors, sizes, rotations, etc.
 *
 * @param <F> the type of the features
 * @param <T> the return type of the feature function
 */

public interface IFeatureFunction<F, T> extends BiFunction<F, Geometry, T>
{}