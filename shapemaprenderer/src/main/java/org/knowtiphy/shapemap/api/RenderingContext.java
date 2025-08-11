/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.shapemap.api;

import java.util.Collection;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;

/** The information the shape map renderer needs to render a map. */
public record RenderingContext<F, E>(
        Collection<? extends IMap<F, E>> maps,
        //  what does this do?
        //        int totalRuleCount,
        E viewPortBounds,
        Rectangle2D screenArea,
        Affine worldToScreen,
        Affine screenToWorld,
        double dScale,
        IFeatureAdapter<F> featureAdapter,
        IRenderablePolygonProvider renderablePolygonProvider,
        ISVGProvider svgProvider,
        ITextAdapter textSizeProvider) {}
