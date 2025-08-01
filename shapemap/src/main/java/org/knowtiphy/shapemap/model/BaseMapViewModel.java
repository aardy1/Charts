package org.knowtiphy.shapemap.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.api.ITextBoundsFunction;
import org.locationtech.jts.geom.Coordinates;
import org.reactfx.Change;
import org.reactfx.EventSource;
import org.reactfx.EventStream;

/**
 * A map view model -- a map model, a viewport, some event streams, SVG provider, etc.
 *
 * @param <S> the type of the schema in the map model
 * @param <F> the type of the features in the map model
 */
public abstract class BaseMapViewModel<S, F> {

    protected final EventSource<Change<Boolean>> layerVisibilityEvent = new EventSource<>();

    protected final EventSource<Change<ReferencedEnvelope>> viewPortBoundsEvent =
            new EventSource<>();

    private final MapViewport viewPort;

    private final IFeatureAdapter<F> featureAdapter;

    private final IRenderablePolygonProvider renderablePolygonProvider;

    private final ISVGProvider svgProvider;

    private final ITextBoundsFunction textSizeProvider;

    private double zoom = 1;

    protected BaseMapViewModel(
            MapViewport viewPort,
            IFeatureAdapter<F> featureAdapter,
            IRenderablePolygonProvider renderablePolygonProvider,
            ISVGProvider svgProvider,
            ITextBoundsFunction textSizeProvider) {
        this.viewPort = viewPort;
        this.featureAdapter = featureAdapter;
        this.renderablePolygonProvider = renderablePolygonProvider;
        this.svgProvider = svgProvider;
        this.textSizeProvider = textSizeProvider;
        //  TODO -- should also have some way of subscribing to add/remove of layers
    }

    public abstract List<MapModel<S, F>> maps();

    public abstract double displayScale();

    public double adjustedDisplayScale() {
        return displayScale() / 2.0;
    }

    public abstract ReferencedEnvelope bounds();

    public abstract void setViewPortBounds(ReferencedEnvelope bounds)
            throws TransformException, NonInvertibleTransformException;

    public MapViewport viewPort() {
        return viewPort;
    }

    public IFeatureAdapter<F> featureAdapter() {
        return featureAdapter;
    }

    public IRenderablePolygonProvider renderablePolygonProvider() {
        return renderablePolygonProvider;
    }

    public ISVGProvider svgProvider() {
        return svgProvider;
    }

    public ITextBoundsFunction textSizeProvider() {
        return textSizeProvider;
    }

    public double zoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;

        var width = bounds().getWidth();
        var height = bounds().getHeight();
        var newWidth = width / zoom();
        var newHeight = height / zoom();
        // expanding/shrinking mutates the envelope so copy it
        var newBounds = new ReferencedEnvelope(bounds());
        newBounds.expandBy((newWidth - width) / 2, (newHeight - height) / 2);
        try {
            setViewPortBounds(newBounds);
        } catch (TransformException | NonInvertibleTransformException ex) {
            Logger.getLogger(Coordinates.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ReferencedEnvelope viewPortBounds() {
        return viewPort.bounds();
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

    public EventStream<Change<Boolean>> layerVisibilityEvent() {
        return layerVisibilityEvent;
    }

    public EventStream<Change<ReferencedEnvelope>> viewPortBoundsEvent() {
        return viewPortBoundsEvent;
    }

    //  TODO -- all maps have the same CRS -- is this too strong an assumption?
    public CoordinateReferenceSystem crs() {
        return bounds().getCoordinateReferenceSystem();
    }
}
