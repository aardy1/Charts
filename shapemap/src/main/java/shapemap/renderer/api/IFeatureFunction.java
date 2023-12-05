/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.api;

import java.util.function.BiFunction;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public interface IFeatureFunction<F extends IFeature, T> extends BiFunction<F, Geometry, T> {

}
