package org.knowtiphy.charts.map;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeatureSource;
import org.knowtiphy.shapemap.api.IMapLayer;
import org.knowtiphy.shapemap.renderer.FeatureTypeStyle;

/**
 * A layer in a map -- a feature source, a style.
 *
 * @param <S> the type of the schema for the feature source
 * @param <F> the type of the features provided by the feature source
 */
public class Layer<F> implements IMapLayer<F, ReferencedEnvelope> {

    //  the feature source containing the features for this layer
    private final IFeatureSource<F, ReferencedEnvelope> featureSource;

    //  the style used to render this layer
    private final FeatureTypeStyle<F> style;

    //  is the layer visible
    private boolean visible;

    //  is the layer scaleless (used to make feature queries faster)
    private final boolean scaleLess;

    public Layer(
            IFeatureSource<F, ReferencedEnvelope> featureSource,
            FeatureTypeStyle<F> style,
            boolean visible,
            boolean scaleLess) {

        this.featureSource = featureSource;
        this.style = style;
        this.visible = visible;
        this.scaleLess = scaleLess;
    }

    @Override
    public IFeatureSource<F, ReferencedEnvelope> featureSource() {
        return featureSource;
    }

    @Override
    public FeatureTypeStyle<F> style() {
        return style;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    //
    //    public boolean isScaleLess() {
    //        return scaleLess;
    //    }

    public String name() {
        return style.featureType();
    }
}