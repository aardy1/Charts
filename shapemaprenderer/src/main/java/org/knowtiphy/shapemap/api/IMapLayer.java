package org.knowtiphy.shapemap.api;

import org.knowtiphy.shapemap.renderer.FeatureTypeStyle;

/**
 * A map layer.
 *
 * @param <S> the type of the schema for the map layers
 * @param <F> the type of the features for the map layers
 */
public interface IMapLayer<F, E> {

    IFeatureSource<F, E> featureSource();

    boolean isVisible();

    FeatureTypeStyle<F> style();
}
