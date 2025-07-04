/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * A source of features.
 *
 * @param <S> the type of the feature schema
 * @param <F> the type of the features conforming to the schema
 */

public interface IFeatureSource<S, F>
{
  IFeatureSourceIterator<F> features();

  IFeatureSourceIterator<F> features(
    ReferencedEnvelope bounds, double displayScale, boolean scaleLess);
}