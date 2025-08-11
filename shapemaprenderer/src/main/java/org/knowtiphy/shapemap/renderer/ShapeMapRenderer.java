/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.shapemap.renderer;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import org.knowtiphy.shapemap.api.IMap;
import org.knowtiphy.shapemap.api.IMapLayer;
import org.knowtiphy.shapemap.api.RenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Rule;

/**
 * @author graham
 */
public class ShapeMapRenderer<F, E> {

    private int featureCount = 0;

    private final RenderingContext<F, E> renderingContext;

    private final GraphicsContext graphics;

    public ShapeMapRenderer(RenderingContext<F, E> renderingContext, GraphicsContext graphics) {
        this.renderingContext = renderingContext;
        this.graphics = graphics;
    }

    public void paint() {

        featureCount = 0;

        var start = System.currentTimeMillis();

        var graphicsRenderingContext =
                new GraphicsRenderingContext<>(
                        graphics,
                        new Transformation(renderingContext.worldToScreen()),
                        renderingContext.featureAdapter(),
                        renderingContext.renderablePolygonProvider(),
                        renderingContext.textSizeProvider(),
                        renderingContext.svgProvider(),
                        onePixelX(renderingContext.screenToWorld()),
                        onePixelY(renderingContext.screenToWorld()));

        try {

            // pass 1 -- do graphics -- point, line and polygon symbolizers
            // Keep track of:
            // a) which rules were applied
            // b) which layers need text layout (had rules that were applied and have text
            // symbolizers
            // c) the maps in reverse order

            var gStart = System.currentTimeMillis();

            var whichMap = 0;
            var reversedMaps = new LinkedList<IMap<F, E>>();
            var layerMap = new HashMap<IMap<F, E>, HashMap<IMapLayer<F, E>, TextInfo>>();

            //  set coordinate xform for all graphics operations
            graphics.setTransform(renderingContext.worldToScreen());

            for (var map : renderingContext.maps()) {
                System.out.println("Map # = " + whichMap);
                reversedMaps.addFirst(map);
                var textInfo =
                        renderGraphics(
                                graphicsRenderingContext,
                                map.layers(),
                                renderingContext.viewPortBounds());
                layerMap.put(map, textInfo);
                whichMap++;
            }

            System.out.println("Graphics time = " + (System.currentTimeMillis() - gStart));

            // pass 2 -- render text using the information computed in pass 1

            var tStart = System.currentTimeMillis();

            //  text symbolizers manage their own transforms
            graphics.setTransform(Transformation.IDENTITY);
            whichMap = reversedMaps.size();
            for (var map : reversedMaps) {
                System.out.println("Map # = " + whichMap);
                renderText(
                        graphicsRenderingContext,
                        map.layers(),
                        renderingContext.viewPortBounds(),
                        layerMap.get(map));
                whichMap--;
            }
            System.out.println("Text time = " + (System.currentTimeMillis() - tStart));

        } catch (Exception ex) {
            System.out.println("Rendering exception");
            ex.printStackTrace(System.err);
        }

        System.out.println("Rendering time " + (System.currentTimeMillis() - start));
        System.out.println("\n\n\n");
        System.out.println("Total features " + featureCount);
    }

    private HashMap<IMapLayer<F, E>, TextInfo> renderGraphics(
            GraphicsRenderingContext<F> context,
            Collection<? extends IMapLayer<F, E>> layers,
            E viewPortBounds)
            throws Exception {

        var layerTextInfo = new HashMap<IMapLayer<F, E>, TextInfo>();

        for (var layer : layers) {
            if (layer.isVisible()) {
                var appliedRule = new boolean[layer.style().rules().size()];
                var layerNeedsTextLayout = false;
                var style = layer.style();
                try (var iterator =
                        layer.featureSource().features(viewPortBounds, renderingContext.dScale())) {
                    for (var feature : iterator) {
                        featureCount++;
                        layerNeedsTextLayout |= applyStyle(style, context, feature, appliedRule);
                    }
                }

                layerTextInfo.put(
                        layer,
                        new TextInfo(
                                layerNeedsTextLayout &= style.hasTextSymbolizers(), appliedRule));
            }
        }

        return layerTextInfo;
    }

    private void renderText(
            GraphicsRenderingContext<F> context,
            Collection<? extends IMapLayer<F, E>> layers,
            E viewPortBounds,
            HashMap<IMapLayer<F, E>, TextInfo> layerMap)
            throws Exception {

        for (var layer : layers) {
            if (layer.isVisible()) {
                var textInfo = layerMap.get(layer);
                if (textInfo.layerNeedsTextLayout()) {
                    try (var iterator =
                            layer.featureSource()
                                    .features(viewPortBounds, renderingContext.dScale())) {
                        for (var feature : iterator) {
                            var rp = 0;
                            for (var rule : layer.style().rules()) {
                                if (textInfo.appliedRule()[rp]) {
                                    applyTextRule(rule, context, feature);
                                }

                                rp++;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean applyStyle(
            FeatureTypeStyle<F> style,
            GraphicsRenderingContext<F> context,
            F feature,
            boolean[] appliedRule) {

        var appliedSomeRule = false;
        //  check if the style even applies
        // if (style.applies(feature))
        var rulePos = 0;
        var elsePos = -1;

        for (var rule : style.rules()) {
            if (!rule.elseFilter()) {
                var applied = applyGraphicsRule(rule, context, feature);
                appliedRule[rulePos] |= applied;
                appliedSomeRule |= applied;
            } else {
                elsePos = rulePos;
            }

            rulePos++;
        }

        if (!appliedSomeRule && elsePos != -1) {
            var applied = applyGraphicsRule(style.rules().get(elsePos), context, feature);
            appliedRule[elsePos] |= applied;
            appliedSomeRule = applied;
        }

        return appliedSomeRule;
    }

    private boolean applyGraphicsRule(
            Rule<F> rule, GraphicsRenderingContext<F> context, F feature) {

        if (rule.filter() != null
                && rule.filter()
                        .apply(feature, context.featureAdapter().defaultGeometry(feature))) {
            for (var symbolizer : rule.graphicSymbolizers()) {
                symbolizer.render(context, feature);
            }

            return true;
        }

        return false;
    }

    private void applyTextRule(Rule<F> rule, GraphicsRenderingContext<F> context, F feature) {
        var featureAdapter = context.featureAdapter();
        if (rule.filter().apply(feature, featureAdapter.defaultGeometry(feature))) {
            for (var symbolizer : rule.textSymbolizers()) {
                symbolizer.render(context, feature);
            }
        }
    }

    private double onePixelX(Affine screenToWorld) {
        var pt1 = screenToWorld.transform(0, 0);
        var pt2 = screenToWorld.transform(1, 0);
        return Math.abs(pt2.getX() - pt1.getX());
    }

    private double onePixelY(Affine screenToWorld) {
        var pt1 = screenToWorld.transform(0, 0);
        var pt2 = screenToWorld.transform(0, 1);
        return Math.abs(pt2.getY() - pt1.getY());
    }
}