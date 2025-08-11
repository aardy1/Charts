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
public interface IFeatureSource<F, E> {

    /**
     * Return all features from the feature source (used mainly in debugging).
     *
     * @return all features
     */
    IFeatureSourceIterator<F> features();

    /**
     * Return all features from the feature source that are within a given bounds and have scamin >=
     * a given display scale.
     *
     * @param bounds the bounds
     * @param dScale the display scale
     * @return all features
     */
    IFeatureSourceIterator<F> features(E bounds, double dScale);
}
