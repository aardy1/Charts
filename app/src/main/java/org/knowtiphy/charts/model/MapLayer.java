package org.knowtiphy.charts.model;

import javafx.beans.property.SimpleBooleanProperty;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeatureSource;
import org.knowtiphy.shapemap.api.IMapLayer;
import org.knowtiphy.shapemap.renderer.FeatureTypeStyle;
import org.reactfx.Change;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

/**
 * A layer in a map model -- a feature source, a style, and some event streams.
 *
 * @param <S> the type of the schema for the feature source
 * @param <F> the type of the features provided by the feature source
 */
public class MapLayer<S, F> implements IMapLayer<S, F, ReferencedEnvelope> {
    private final IFeatureSource<S, F, ReferencedEnvelope> featureSource;

    private final FeatureTypeStyle<S, F> style;

    private final SimpleBooleanProperty visible = new SimpleBooleanProperty();

    private final boolean scaleLess;

    private final EventStream<Change<Boolean>> layerVisibilityEvent =
            EventStreams.changesOf(visible);

    public MapLayer(
            IFeatureSource<S, F, ReferencedEnvelope> featureSource,
            FeatureTypeStyle<S, F> style,
            boolean visible,
            boolean scaleLess) {
        this.featureSource = featureSource;
        this.style = style;
        this.visible.set(visible);
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
        return visible.get();
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }

    @Override
    public boolean isScaleLess() {
        return scaleLess;
    }

    public EventStream<Change<Boolean>> layerVisibilityEvent() {
        return layerVisibilityEvent;
    }
}