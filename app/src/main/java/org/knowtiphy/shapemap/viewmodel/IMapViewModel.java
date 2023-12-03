/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.viewmodel;

import java.util.Collection;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.renderer.context.ISVGProvider;
import org.knowtiphy.shapemap.renderer.context.RenderGeomCache;
import org.reactfx.Change;
import org.reactfx.EventSource;

/**
 * @author graham
 */
public interface IMapViewModel {

	Collection<MapLayer> layers();

	ReferencedEnvelope viewPortBounds();

	CoordinateReferenceSystem crs();

	int totalRuleCount();

	RenderGeomCache renderGeomCache();

	ISVGProvider svgCache();

	EventSource<Change<ReferencedEnvelope>> viewPortBoundsEvent();

	EventSource<Change<Boolean>> layerVisibilityEvent();

	EventSource<Change<IMapViewModel>> newMapEvent();

}
