/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.shapemap.api;

import java.util.Collection;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import org.geotools.geometry.jts.ReferencedEnvelope;

/** The information the shape map renderer needs to render a map. */
public record RenderingContext<S, F>(
        Collection<? extends IMapLayer<S, F>> layers,
        //  what does this do?
        int totalRuleCount,
        ReferencedEnvelope viewPortBounds,
        Rectangle2D screenArea,
        Affine worldToScreen,
        Affine screenToWorld,
        double displayScale,
        IFeatureAdapter<F> featureAdapter,
        IRenderablePolygonProvider renderablePolygonProvider,
        ISVGProvider svgProvider,
        ITextBoundsFunction textSizeProvider) {}
