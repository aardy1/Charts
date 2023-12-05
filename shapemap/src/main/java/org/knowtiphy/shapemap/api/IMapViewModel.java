/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import java.util.Collection;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.model.MapLayer;
import org.knowtiphy.shapemap.renderer.context.RenderGeomCache;
import org.reactfx.Change;
import org.reactfx.EventStream;

/**
 * @author graham
 */
public interface IMapViewModel<S, F extends IFeature> {

	// used by the renderer

	Collection<MapLayer<S, F>> layers();

	ReferencedEnvelope viewPortBounds();

	CoordinateReferenceSystem crs();

	int totalRuleCount();

	RenderGeomCache renderGeomCache();

	ISVGProvider svgCache();

	// used externally to change view model state

	EventStream<Change<Boolean>> layerVisibilityEvent();

	EventStream<Change<ReferencedEnvelope>> viewPortBoundsEvent();

	EventStream<Change<IMapViewModel<S, F>>> newMapViewModel();

	void setViewPortScreenArea(Rectangle2D screenArea) throws TransformException, NonInvertibleTransformException;

	void setLayerVisible(S type, boolean visible);

}
