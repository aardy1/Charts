/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * A source of features of a given schema type.
 *
 * @param <S> the type of the schema
 * @param <F> the type of the features
 */
public interface IFeatureSource<S, F>
{

  IFeatureSourceIterator<F> features();

  IFeatureSourceIterator<F> features(
    ReferencedEnvelope bounds, double displayScale, boolean scaleLess);

  S getSchema();

}