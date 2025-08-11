package org.knowtiphy.shapemap.api;

import java.util.Collection;

/**
 * A map layer.
 *
 * @param <F> the type of the features for the map layers
 * @param <E> the type of bounding envelope in the feature source for a layer
 */
public interface IMap<F, E> {
    Collection<? extends IMapLayer<F, E>> layers();
}
