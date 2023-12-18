/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.context;

import javafx.geometry.Rectangle2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.api.ITextSizeProvider;
import org.knowtiphy.shapemap.model.MapLayer;

import java.util.Collection;

/**
 * @author graham
 */
public record RendererContext<S, F>(
// @formatter:off
		Collection<MapLayer<S, F>> layers,
		int totalRuleCount,
		ReferencedEnvelope viewPortBounds,
		Rectangle2D paintArea,
		IFeatureAdapter<F> featureAdapter,
		IRenderablePolygonProvider renderablePolygonProvider,
		ISVGProvider svgProvider,
    ITextSizeProvider textSizeProvider)
{}
// @formatter:on