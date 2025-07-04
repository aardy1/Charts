package org.knowtiphy.shapemap.model;

import javafx.beans.property.SimpleBooleanProperty;
import org.knowtiphy.shapemap.api.IFeatureSource;
import org.knowtiphy.shapemap.renderer.FeatureTypeStyle;
import org.reactfx.Change;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

/**
 * A layer in a map model -- a feature source, a style, and some event streams.
 *
 * @param <S> the type of the schema for the feature source
 * @param <F> the type of the features provded by the feature source
 */

public class MapLayer<S, F>
{
    private final IFeatureSource<S, F> featureSource;

    private final FeatureTypeStyle<S, F> style;

    private final SimpleBooleanProperty visible = new SimpleBooleanProperty();

    private final boolean scaleLess;

    private final EventStream<Change<Boolean>> layerVisibilityEvent = EventStreams.changesOf(
        visible);

    public MapLayer(
        IFeatureSource<S, F> featureSource, FeatureTypeStyle<S, F> style, boolean visible,
        boolean scaleLess)
    {
        this.featureSource = featureSource;
        this.style = style;
        this.visible.set(visible);
        this.scaleLess = scaleLess;
    }

    public IFeatureSource<S, F> featureSource()
    {
        return featureSource;
    }

    public FeatureTypeStyle<S, F> style()
    {
        return style;
    }

    public boolean isVisible()
    {
        return visible.get();
    }

    public void setVisible(boolean visible)
    {
        this.visible.set(visible);
    }

    public boolean isScaleLess()
    {
        return scaleLess;
    }

    public EventStream<Change<Boolean>> layerVisibilityEvent()
    {
        return layerVisibilityEvent;
    }
}