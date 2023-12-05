package org.knowtiphy.shapemap.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.api.IMapViewModel;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.model.MapLayer;
import org.knowtiphy.shapemap.model.MapViewport;
import org.knowtiphy.shapemap.renderer.context.RenderGeomCache;
import org.reactfx.Change;
import org.reactfx.EventSource;

public class MapViewModel<S, F extends IFeature> implements IMapViewModel<S, F> {

	private final EventSource<Change<Boolean>> layerVisibilityEvent = new EventSource<>();

	private final EventSource<Change<ReferencedEnvelope>> viewPortBoundsEvent = new EventSource<>();

	private final EventSource<Change<IMapViewModel<S, F>>> newMapEvent = new EventSource<>();

	private final ReferencedEnvelope bounds;

	private final Map<S, MapLayer<S, F>> layers = new LinkedHashMap<>();

	private final MapViewport viewport;

	private final RenderGeomCache renderGeomCache = new RenderGeomCache();

	private final ISVGProvider svgCache;

	// possibly shouldnt be here -- but it makes for faster rendering
	private int totalRuleCount;

	public MapViewModel(ReferencedEnvelope envelope, ISVGProvider svgCache)
			throws TransformException, NonInvertibleTransformException, FactoryException {

		this.bounds = envelope;
		this.svgCache = svgCache;

		viewport = new MapViewport();
		viewport.setBounds(envelope);
		viewport.setCoordinateReferenceSystem(envelope.getCoordinateReferenceSystem());
	}

	@Override
	public Collection<MapLayer<S, F>> layers() {
		return layers.values();
	}

	@Override
	public int totalRuleCount() {
		return totalRuleCount;
	}

	@Override
	public ReferencedEnvelope viewPortBounds() {
		return viewport.getBounds();
	}

	@Override
	public CoordinateReferenceSystem crs() {
		return bounds.getCoordinateReferenceSystem();
	}

	@Override
	public RenderGeomCache renderGeomCache() {
		return renderGeomCache;
	}

	@Override
	public ISVGProvider svgCache() {
		return svgCache;
	}

	@Override
	public void setViewPortScreenArea(Rectangle2D screenArea)
			throws TransformException, NonInvertibleTransformException {
		viewport.setScreenArea(screenArea);
	}

	@Override
	public EventSource<Change<Boolean>> layerVisibilityEvent() {
		return layerVisibilityEvent;
	}

	@Override
	public EventSource<Change<ReferencedEnvelope>> viewPortBoundsEvent() {
		return viewPortBoundsEvent;
	}

	@Override
	public EventSource<Change<IMapViewModel<S, F>>> newMapViewModel() {
		return newMapEvent;
	}

	public MapLayer<S, F> layer(S type) {
		return layers.get(type);
	}

	public void addLayer(MapLayer<S, F> layer) {

		layers.put(layer.getFeatureSource().getSchema(), layer);
		totalRuleCount += layer.getStyle().rules().size();
	}

	public ReferencedEnvelope bounds() {
		return bounds;
	}

	public void setViewPortBounds(ReferencedEnvelope bounds)
			throws TransformException, NonInvertibleTransformException {

		var oldBounds = viewport.getBounds();
		viewport.setBounds(bounds);
		System.err.println("\n\nVP change " + bounds + "\n\n");
		viewPortBoundsEvent.push(new Change<>(oldBounds, bounds()));
	}

	public Rectangle2D viewPortScreenArea() {
		return viewport.getScreenArea();
	}

	public Affine viewPortScreenToWorld() {
		return viewport.getScreenToWorld();
	}

	public Affine viewPortWorldToScreen() {
		return viewport.getWorldToScreen();
	}

	public void setLayerVisible(S type, boolean visible) {
		var layer = layer(type);
		var oldVisible = layer.isVisible();
		layer.setVisible(visible);
		layerVisibilityEvent.push(new Change<>(oldVisible, visible));
	}

	// public void setViewPortScreenArea(Rectangle2D screenArea)
	// throws TransformException, NonInvertibleTransformException {
	// viewport.setScreenArea(screenArea);
	// }

	// public ReferencedEnvelope bounds() {
	// return bounds;
	// }

	// public Rectangle2D viewPortScreenArea() {
	// return viewport.getScreenArea();
	// }

	// public Affine viewPortScreenToWorld() {
	// return viewport.getScreenToWorld();
	// }

	// public Affine viewPortWorldToScreen() {
	// return viewport.getWorldToScreen();
	// }

}