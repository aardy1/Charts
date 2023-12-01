/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.basic;

import java.util.function.BiFunction;
import org.geotools.api.feature.simple.SimpleFeature;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public interface IFeatureFunction<T> extends BiFunction<SimpleFeature, Geometry, T> {

}
