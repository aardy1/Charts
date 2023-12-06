/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.context;

import java.util.Collection;
import javafx.geometry.Rectangle2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.api.model.MapLayer;

/**
 * @author graham
 */
public record RendererContext<S, F> (
// @formatter:off
		Collection<MapLayer<S, F>> layers,
		int totalRuleCount,
		ReferencedEnvelope viewPortBounds,
		Rectangle2D paintArea,
		IFeatureAdapter<F> featureAdapter,
		IRenderablePolygonProvider renderablePolygonProvider,
		ISVGProvider svgProvider)
{}
// @formatter:on
