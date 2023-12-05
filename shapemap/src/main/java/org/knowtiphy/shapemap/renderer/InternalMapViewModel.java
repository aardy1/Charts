package org.knowtiphy.shapemap.renderer;

import java.util.Collection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.api.IMapViewModel;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.model.MapLayer;
import org.reactfx.EventSource;
import org.reactfx.EventStream;

public class InternalMapViewModel<S, F extends IFeature> {

	private final EventSource<Boolean> layoutNeededEvent = new EventSource<>();

	private final IMapViewModel<S, F> mapViewModel;

	public InternalMapViewModel(IMapViewModel<S, F> mapViewModel) {

		this.mapViewModel = mapViewModel;
		mapViewModel.viewPortBoundsEvent().subscribe(x -> layoutNeeded());
		mapViewModel.layerVisibilityEvent().subscribe(x -> layoutNeeded());
		mapViewModel.newMapViewModel().subscribe(x -> layoutNeeded());
	}

	public Collection<MapLayer<S, F>> layers() {
		return mapViewModel.layers();
	}

	public int totalRuleCount() {
		return mapViewModel.totalRuleCount();
	}

	public ReferencedEnvelope viewPortBounds() {
		return mapViewModel.viewPortBounds();
	}

	public IRenderablePolygonProvider renderablePolygonProvider() {
		return mapViewModel.renderablePolygonProvider();
	}

	public ISVGProvider svgProvider() {
		return mapViewModel.svgProvider();
	}

	public EventStream<Boolean> layoutNeededEvent() {
		return layoutNeededEvent;
	}

	private void layoutNeeded() {
		layoutNeededEvent.push(true);
	}

}