/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.viewmodel;

import java.util.Collection;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.renderer.api.ISVGProvider;
import org.knowtiphy.shapemap.renderer.context.RenderGeomCache;
import org.knowtiphy.shapemap.renderer.api.IFeature;
import org.reactfx.Change;
import org.reactfx.EventSource;

/**
 * @author graham
 */
public interface IMapViewModel<S, F extends IFeature> {

	Collection<MapLayer<S, F>> layers();

	ReferencedEnvelope viewPortBounds();

	CoordinateReferenceSystem crs();

	int totalRuleCount();

	RenderGeomCache renderGeomCache();

	ISVGProvider svgCache();

	EventSource<Change<ReferencedEnvelope>> viewPortBoundsEvent();

	EventSource<Change<Boolean>> layerVisibilityEvent();

	EventSource<Change<IMapViewModel<S, F>>> newMapEvent();

}
