/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.shapemap.renderer.context;

import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.model.MapLayer;

import java.util.Collection;
import org.knowtiphy.shapemap.api.ITextBoundsFunction;

/**
 * @author graham
 */

//@formatter:off
public record RenderingContext<S, F>
(
    Collection<MapLayer<S, F>> layers,
    int totalRuleCount,
    ReferencedEnvelope viewPortBounds,
    Rectangle2D paintArea,
    Affine worldToScreen,
    Affine screenToWorld,
    double displayScale,
    IFeatureAdapter<F> featureAdapter,
    IRenderablePolygonProvider renderablePolygonProvider,
    ISVGProvider svgProvider,
    ITextBoundsFunction textSizeProvider
) {}
//@formatter:on