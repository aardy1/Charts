package org.knowtiphy.charts.map;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeatureSource;
import org.knowtiphy.shapemap.api.IMapLayer;
import org.knowtiphy.shapemap.renderer.FeatureTypeStyle;

/**
 * A layer in a map -- a feature source, a style, and some event streams.
 *
 * @param <S> the type of the schema for the feature source
 * @param <F> the type of the features provided by the feature source
 */
public class Layer<S, F> implements IMapLayer<S, F, ReferencedEnvelope> {
    private final IFeatureSource<S, F, ReferencedEnvelope> featureSource;

    private final FeatureTypeStyle<S, F> style;

    private boolean visible;

    private final boolean scaleLess;

    public Layer(
            IFeatureSource<S, F, ReferencedEnvelope> featureSource,
            FeatureTypeStyle<S, F> style,
            boolean visible,
            boolean scaleLess) {

        this.featureSource = featureSource;
        this.style = style;
        this.visible = visible;
        this.scaleLess = scaleLess;
    }

    @Override
    public IFeatureSource<S, F, ReferencedEnvelope> featureSource() {
        return featureSource;
    }

    @Override
    public FeatureTypeStyle<S, F> style() {
        return style;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isScaleLess() {
        return scaleLess;
    }

    public String name() {
        return style.featureType();
    }
}