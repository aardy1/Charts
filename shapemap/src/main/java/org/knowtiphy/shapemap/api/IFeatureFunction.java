/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import java.util.function.BiFunction;
import org.locationtech.jts.geom.Geometry;

/**
 * A function from a feature to a value -- the functions are used in filtering (values are
 * booleans), styling (values are colors, sizes, rotations, etc), etc.
 *
 * @param <F> the type of the features
 * @param <T> the return type of the feature function
 */

public interface IFeatureFunction<F, T> extends BiFunction<F, Geometry, T> {

}
