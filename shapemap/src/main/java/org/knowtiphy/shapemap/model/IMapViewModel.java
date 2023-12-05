/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.model;

import java.util.Collection;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.renderer.api.IFeature;
import org.knowtiphy.shapemap.renderer.api.ISVGProvider;
import org.knowtiphy.shapemap.renderer.context.RenderGeomCache;
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

	void setViewPortScreenArea(Rectangle2D screenArea) throws TransformException, NonInvertibleTransformException;

	EventSource<Change<ReferencedEnvelope>> viewPortBoundsEvent();

	EventSource<Change<Boolean>> layerVisibilityEvent();

	EventSource<Change<IMapViewModel<S, F>>> newMapEvent();

}
