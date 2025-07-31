package org.knowtiphy.shapemap.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.api.ITextSizeProvider;
import org.reactfx.Change;

/**
 * A map view model for a single map -- a map model, a viewport, and some event streams.
 *
 * @param <S> the type of the schema in the map model
 * @param <F> the type of the features in the map model
 */
public abstract class MapViewModel<S, F> extends BaseMapViewModel<S, F> {
    private final MapModel<S, F> map;

    private final MapViewport viewPort;

    protected MapViewModel(
            MapModel<S, F> map,
            MapViewport viewPort,
            IFeatureAdapter<F> featureAdapter,
            IRenderablePolygonProvider renderablePolygonProvider,
            ISVGProvider svgProvider,
            ITextSizeProvider textSizeProvider) {
        super(featureAdapter, renderablePolygonProvider, svgProvider, textSizeProvider);
        this.map = map;
        this.viewPort = viewPort;

        for (var layer : map.layers()) {
            layer.layerVisibilityEvent().feedTo(layerVisibilityEvent);
        }

        //  TODO -- should also have some way of subscribing to add/remove of layers
    }

    public MapModel<S, F> map() {
        return map;
    }

    public ReferencedEnvelope bounds() {
        return map.bounds();
    }

    public ReferencedEnvelope viewPortBounds() {
        return viewPort.bounds();
    }

    public void setViewPortBounds(ReferencedEnvelope bounds)
            throws TransformException, NonInvertibleTransformException {
        var newExtent = clip(bounds);
        if (!newExtent.equals(viewPortBounds())) {
            var oldBounds = viewPort.bounds();
            viewPort.setBounds(newExtent);
            viewPortBoundsEvent.push(new Change<>(oldBounds, newExtent));
        }
    }

    public Rectangle2D viewPortScreenArea() {
        return viewPort.screenArea();
    }

    public void setViewPortScreenArea(Rectangle2D screenArea)
            throws TransformException, NonInvertibleTransformException {
        viewPort.setScreenArea(screenArea);
    }

    public Affine viewPortScreenToWorld() {
        return viewPort.screenToWorld();
    }

    public Affine viewPortWorldToScreen() {
        return viewPort.worldToScreen();
    }

    private ReferencedEnvelope clip(ReferencedEnvelope envelope) {
        var maxExtent = map.bounds();

        var width = Math.min(envelope.getWidth(), maxExtent.getWidth());
        var height = Math.min(envelope.getHeight(), maxExtent.getHeight());

        var minX = envelope.getMinX();
        var maxX = envelope.getMaxX();
        var minY = envelope.getMinY();
        var maxY = envelope.getMaxY();

        if (maxX > maxExtent.getMaxX()) {
            maxX = maxExtent.getMaxX();
            minX = Math.max(maxExtent.getMinX(), maxX - width);
        } else if (minX < maxExtent.getMinX()) {
            minX = maxExtent.getMinX();
            maxX = Math.min(maxExtent.getMaxX(), minX + width);
        }

        if (maxY > maxExtent.getMaxY()) {
            maxY = maxExtent.getMaxY();
            minY = Math.max(maxExtent.getMinY(), maxY - height);
        } else if (minY < maxExtent.getMinY()) {
            minY = maxExtent.getMinY();
            maxY = Math.min(maxExtent.getMaxY(), minY + height);
        }

        assert width >= 0;
        assert width <= maxExtent.getWidth();
        assert height >= 0;
        assert height <= maxExtent.getHeight();

        assert minX <= maxExtent.getMaxX() : minX + "::" + maxExtent.getMaxX();
        assert minX >= maxExtent.getMinX() : minX;
        assert maxX <= maxExtent.getMaxX() : maxX;
        assert maxX >= maxExtent.getMinX() : maxX;

        assert minY <= maxExtent.getMaxY() : minY + "::" + maxExtent.getMaxY();
        assert minY >= maxExtent.getMinY() : minY + "::" + maxExtent.getMinY();
        assert maxY <= maxExtent.getMaxY() : maxY + "::" + maxExtent.getMaxY();
        assert maxY >= maxExtent.getMinY() : maxY + "::" + maxExtent.getMaxY();

        return new ReferencedEnvelope(minX, maxX, minY, maxY, map.crs());
    }
}