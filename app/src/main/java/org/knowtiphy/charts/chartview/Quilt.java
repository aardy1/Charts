package org.knowtiphy.charts.chartview;

import java.util.List;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.api.ITextBoundsFunction;
import org.knowtiphy.charts.chartview.view.model.MapModel;
import org.reactfx.Change;

/**
 * A map view model for a collection of map models quilted together.
 *
 * @param <S> the type of the schema for the map models
 * @param <F> the type of the features in the map models
 */
public class Quilt<S, F> extends BaseMapViewModel<S, F> {

    private List<MapModel<S, F>> maps;

    private final MapViewport viewPort;

    protected Quilt(
            List<MapModel<S, F>> maps,
            MapViewport viewPort,
            IFeatureAdapter<F> featureAdapter,
            IRenderablePolygonProvider renderablePolygonProvider,
            ISVGProvider svgProvider,
            ITextBoundsFunction textSizeProvider) {
        super(viewPort, featureAdapter, renderablePolygonProvider, svgProvider, textSizeProvider);
        this.viewPort = viewPort;
        this.maps = maps;

        for (var map : maps) {
            for (var layer : map.layers()) {
                layer.layerVisibilityEvent().feedTo(layerVisibilityEvent);
            }
        }

        //  TODO -- should also have some way of subscribing to add/remove of layers
    }

    public List<MapModel<S, F>> maps() {
        return maps;
    }

    public void setMaps(List<MapModel<S, F>> maps) {
        this.maps = maps;
    }

    public void setViewPortBounds(ReferencedEnvelope bounds)
            throws TransformException, NonInvertibleTransformException {
        System.err.println("Set view port bounds = " + bounds);
        var oldBounds = viewPort.bounds();
        viewPort.setBounds(bounds);
        viewPortBoundsEvent.push(new Change<>(oldBounds, bounds));
    }

    @Override
    public double displayScale() {
        return maps().get(0).cScale() / zoom();
    }

    @Override
    public ReferencedEnvelope bounds() {
        return viewPort.bounds();
    }
}
