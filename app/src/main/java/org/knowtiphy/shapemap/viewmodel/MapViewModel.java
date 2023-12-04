package org.knowtiphy.shapemap.viewmodel;

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
import org.knowtiphy.charts.ontology.S57;
import org.knowtiphy.shapemap.renderer.context.ISVGProvider;
import org.knowtiphy.shapemap.renderer.context.RenderGeomCache;
import org.knowtiphy.shapemap.renderer.feature.IFeature;
import org.reactfx.Change;
import org.reactfx.EventSource;

public class MapViewModel<S, F extends IFeature> implements IMapViewModel<S, F> {

	private final EventSource<Change<Boolean>> layerVisibilityEvent = new EventSource<>();

	private final EventSource<Change<ReferencedEnvelope>> viewPortBoundsEvent = new EventSource<>();

	// TODO -- need to unsubscibe?
	private final EventSource<Change<IMapViewModel<S, F>>> newMapEvent = new EventSource<>();

	private final ReferencedEnvelope bounds;

	private final MapDisplayOptions displayOptions;

	private final Map<String, MapLayer<S, F>> layers = new LinkedHashMap<>();

	private final String title;

	private MapViewport viewport;

	private final RenderGeomCache renderGeomCache = new RenderGeomCache();

	private final ISVGProvider svgCache;

	// possibly shouldnt be here -- but it makes for faster rendering
	private int totalRuleCount;

	public MapViewModel(String title, ReferencedEnvelope envelope, MapDisplayOptions displayOptions,
			ISVGProvider svgCache) throws TransformException, NonInvertibleTransformException, FactoryException {

		this.title = title;
		this.bounds = envelope;
		this.displayOptions = displayOptions;
		this.svgCache = svgCache;

		viewport = new MapViewport();
		viewport.setBounds(envelope);
		viewport.setCoordinateReferenceSystem(envelope.getCoordinateReferenceSystem());

		displayOptions.showLightsEvents.subscribe(change -> setLayerVisible(change, S57.OC_LIGHTS));
		displayOptions.showPlatformEvents.subscribe(change -> setLayerVisible(change, S57.OC_OFSPLF));
		displayOptions.showSoundingsEvents.subscribe(change -> setLayerVisible(change, S57.OC_SOUNDG));
		displayOptions.showWreckEvents.subscribe(change -> setLayerVisible(change, S57.OC_WRECKS));
	}

	public void addLayer(MapLayer<S, F> layer)
			throws TransformException, FactoryException, NonInvertibleTransformException {
		layers.put(layer.getFeatureSource().getSchema().getTypeName(), layer);
		totalRuleCount += layer.getStyle().rules().size();
		checkViewportCRS();
	}

	@Override
	public Collection<MapLayer<S, F>> layers() {
		return layers.values();
	}

	public ReferencedEnvelope bounds() {
		return bounds;
	}

	public double getZoomFactor() {
		return 1 / (viewPortBounds().getWidth() / bounds().getWidth());
	}

	public MapDisplayOptions displayOptions() {
		return displayOptions;
	}

	@Override
	public int totalRuleCount() {
		return totalRuleCount;
	}

	@Override
	public EventSource<Change<ReferencedEnvelope>> viewPortBoundsEvent() {
		return viewPortBoundsEvent;
	}

	@Override
	public EventSource<Change<Boolean>> layerVisibilityEvent() {
		return layerVisibilityEvent;
	}

	@Override
	public EventSource<Change<IMapViewModel<S, F>>> newMapEvent() {
		return newMapEvent;
	}

	public MapViewport getViewport() {
		return viewport;
	}

	public void setViewport(MapViewport viewport) {
		this.viewport = viewport;
	}

	public MapLayer<S, F> layer(String type) {
		return layers.get(type);
	}

	@Override
	public ReferencedEnvelope viewPortBounds() {
		return viewport.getBounds();
	}

	public void setViewPortBounds(ReferencedEnvelope bounds)
			throws TransformException, NonInvertibleTransformException {

		var oldBounds = viewport.getBounds();
		viewport.setBounds(bounds);
		System.err.println("\n\nVP change " + bounds + "\n\n");
		viewPortBoundsEvent.push(new Change<>(oldBounds, bounds));
	}

	public Rectangle2D viewPortScreenArea() {
		return viewport.getScreenArea();
	}

	public void setViewPortScreenArea(Rectangle2D screenArea)
			throws TransformException, NonInvertibleTransformException {
		viewport.setScreenArea(screenArea);
	}

	public Affine viewPortScreenToWorld() {
		return viewport.getScreenToWorld();
	}

	public Affine viewPortWorldToScreen() {
		return viewport.getWorldToScreen();
	}

	@Override
	public CoordinateReferenceSystem crs() {
		return bounds.getCoordinateReferenceSystem();
	}

	public String title() {
		return title;
	}

	@Override
	public RenderGeomCache renderGeomCache() {
		return renderGeomCache;
	}

	@Override
	public ISVGProvider svgCache() {
		return svgCache;
	}

	/**
	 * Sets the CRS of the viewport, if one exists, based on the first Layer with a
	 * non-null CRS. This is called when a new Layer is added to the Layer list. Does
	 * nothing if the viewport already has a CRS set or if it has been set as
	 * non-editable.
	 */
	private void checkViewportCRS() throws TransformException, FactoryException, NonInvertibleTransformException {

		if (viewport != null && crs() == null) {
			for (MapLayer layer : layers()) {
				var bnds = layer.getBounds();
				if (bnds != null) {
					CoordinateReferenceSystem crs = bnds.getCoordinateReferenceSystem();
					if (crs != null) {
						viewport.setCoordinateReferenceSystem(crs);
						return;
					}
				}
			}
		}
	}

	private void setLayerVisible(Change<Boolean> change, String type) {
		var layer = layer(type);
		if (layer != null) {
			layer.setVisible(change.getNewValue());
			layerVisibilityEvent.push(change);
		}
	}

}

// public ReferencedEnvelope getMaxBounds() throws TransformException, FactoryException {
//
//		return bounds;
		// CoordinateReferenceSystem mapCrs = viewport.getCoordinateReferenceSystem();
		//
		// ReferencedEnvelope maxBounds = null;
		//
		// for (MapLayer layer : layers) {
		// var layerBounds = layer.getBounds();
		// if (layerBounds == null || layerBounds.isEmpty() || layerBounds.isNull()) {
		// continue;
		// }
		// if (mapCrs == null) {
		// // crs for the map is not defined; let us start with the first CRS
		// // we see
		// // then!
		// maxBounds = new ReferencedEnvelope(layerBounds);
		// mapCrs = layerBounds.getCoordinateReferenceSystem();
		// continue;
		// }
		// ReferencedEnvelope normalized;
		// if (CRS.equalsIgnoreMetadata(mapCrs,
		// layerBounds.getCoordinateReferenceSystem())) {
		// normalized = layerBounds;
		// }
		// else {
		// normalized = layerBounds.transform(mapCrs, true);
		// }
		//
		// if (maxBounds == null) {
		// maxBounds = normalized;
		// }
		// else {
		// maxBounds.expandToInclude(normalized);
		// }
		// }
		//
		// if (maxBounds == null) {
		// maxBounds = new ReferencedEnvelope(mapCrs);
		// }
		//
		// return maxBounds;
//	}