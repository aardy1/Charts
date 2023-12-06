package org.knowtiphy.shapemap.api.model;

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
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.api.ISchemaAdapter;
import org.reactfx.Change;
import org.reactfx.EventSource;
import org.reactfx.EventStream;

public abstract class MapViewModel<S, F> {

	private final EventSource<Change<Boolean>> layerVisibilityEvent = new EventSource<>();

	private final EventSource<Change<ReferencedEnvelope>> viewPortBoundsEvent = new EventSource<>();

	private final EventSource<Change<Rectangle2D>> viewPortScreenAreaEvent = new EventSource<>();

	private final EventSource<Change<MapViewModel<S, F>>> newMapViewModel = new EventSource<>();

	private final ReferencedEnvelope bounds;

	private final Map<String, MapLayer<S, F>> layers = new LinkedHashMap<>();

	private final MapViewport viewPort;

	private final IFeatureAdapter<F> featureAdapter;

	private final ISchemaAdapter<S> schemaAdapter;

	private final IRenderablePolygonProvider renderablePolygonProvider;

	private final ISVGProvider svgProvider;

	// possibly shouldnt be here -- but it makes for faster rendering
	private int totalRuleCount;

	protected MapViewModel(ReferencedEnvelope bounds, ISchemaAdapter<S> schemaAdapter,
			IFeatureAdapter<F> featureAdapter, IRenderablePolygonProvider renderablePolygonProvider,
			ISVGProvider svgProvider) throws TransformException, NonInvertibleTransformException, FactoryException {

		this.bounds = bounds;
		this.schemaAdapter = schemaAdapter;
		this.featureAdapter = featureAdapter;
		this.renderablePolygonProvider = renderablePolygonProvider;
		this.svgProvider = svgProvider;
		this.totalRuleCount = 0;

		viewPort = new MapViewport(bounds);
	}

	public IFeatureAdapter<F> featureAdapter() {
		return featureAdapter;
	}

	protected MapViewport viewPort() {
		return viewPort;
	}

	public Collection<MapLayer<S, F>> layers() {
		return layers.values();
	}

	public void addLayer(MapLayer<S, F> layer) {
		layers.put(schemaAdapter.name(layer.getFeatureSource().getSchema()), layer);
		totalRuleCount += layer.getStyle().rules().size();
	}

	public MapLayer<S, F> layer(String type) {
		return layers.get(type);
	}

	public int totalRuleCount() {
		return totalRuleCount;
	}

	public void setLayerVisible(String type, boolean visible) {
		var layer = layer(type);
		var oldVisible = layer.isVisible();
		layer.setVisible(visible);
		layerVisibilityEvent.push(new Change<>(oldVisible, visible));
	}

	public ReferencedEnvelope viewPortBounds() {
		return viewPort.getBounds();
	}

	public void setViewPortBounds(ReferencedEnvelope bounds)
			throws TransformException, NonInvertibleTransformException {

		var oldBounds = viewPort.getBounds();
		viewPort.setBounds(bounds);
		System.err.println("\n\nVP change " + bounds + "\n\n");
		viewPortBoundsEvent.push(new Change<>(oldBounds, bounds));
	}

	public Rectangle2D viewPortScreenArea() {
		return viewPort().getScreenArea();
	}

	public void setViewPortScreenArea(Rectangle2D screenArea)
			throws TransformException, NonInvertibleTransformException {
		var oldScreenArea = viewPort.getScreenArea();
		viewPort.setScreenArea(screenArea);
		viewPortScreenAreaEvent.push(new Change<>(oldScreenArea, screenArea));
	}

	public IRenderablePolygonProvider renderablePolygonProvider() {
		return renderablePolygonProvider;
	}

	public ISVGProvider svgProvider() {
		return svgProvider;
	}

	public void setNewMapViewModel(MapViewModel<S, F> map) {
		newMapViewModel.push(new Change<>(this, map));
	}

	public ReferencedEnvelope bounds() {
		return bounds;
	}

	public CoordinateReferenceSystem crs() {
		return bounds.getCoordinateReferenceSystem();
	}

	public Affine viewPortScreenToWorld() {
		return viewPort.getScreenToWorld();
	}

	public Affine viewPortWorldToScreen() {
		return viewPort.getWorldToScreen();
	}

	public EventStream<Change<Boolean>> layerVisibilityEvent() {
		return layerVisibilityEvent;
	}

	public EventStream<Change<ReferencedEnvelope>> viewPortBoundsEvent() {
		return viewPortBoundsEvent;
	}

	public EventStream<Change<Rectangle2D>> viewPortScreenEvent() {
		return viewPortScreenAreaEvent;
	}

	public EventStream<Change<MapViewModel<S, F>>> newMapViewModel() {
		return newMapViewModel;
	}

}