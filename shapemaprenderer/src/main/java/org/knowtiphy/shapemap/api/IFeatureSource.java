/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.shapemap.api;

/**
 * A source of features.
 *
 * @param <S> the type of the feature schema
 * @param <F> the type of the features conforming to the schema
 */
public interface IFeatureSource<S, F, E> {

    IFeatureSourceIterator<F> features();

    IFeatureSourceIterator<F> features(E bounds, double displayScale, boolean scaleLess);
}
