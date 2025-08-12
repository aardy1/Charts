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
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IMap;
import org.knowtiphy.shapemap.api.IMapLayer;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.api.ITextAdapter;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Rule;

/**
 * @author graham
 */
public class ShapeMapRenderer<F, E> {

    private int featureCount = 0;

    private final GraphicsContext graphics;

    private final Collection<? extends IMap<F, E>> maps;
    //  what does this do?
    //        int totalRuleCount,
    private final E viewPortBounds;
    private final Affine worldToScreen;
    private final Affine screenToWorld;
    private final double dScale;
    private final IFeatureAdapter<F> featureAdapter;
    private final IRenderablePolygonProvider<F> renderablePolygonProvider;
    private final ISVGProvider svgProvider;
    private final ITextAdapter textSizeProvider;

    public ShapeMapRenderer(
            GraphicsContext graphics,
            Collection<? extends IMap<F, E>> maps,
            E viewPortBounds,
            Affine worldToScreen,
            Affine screenToWorld,
            double dScale,
            IFeatureAdapter<F> featureAdapter,
            IRenderablePolygonProvider<F> renderablePolygonProvider,
            ISVGProvider svgProvider,
            ITextAdapter textSizeProvider) {

        this.graphics = graphics;
        this.maps = maps;
        this.viewPortBounds = viewPortBounds;
        this.worldToScreen = worldToScreen;
        this.screenToWorld = screenToWorld;
        this.dScale = dScale;
        this.featureAdapter = featureAdapter;
        this.renderablePolygonProvider = renderablePolygonProvider;
        this.svgProvider = svgProvider;
        this.textSizeProvider = textSizeProvider;
    }

    public void paint() {

        featureCount = 0;

        var start = System.currentTimeMillis();

        var graphicsRenderingContext =
                new RenderingContext<>(
                        graphics,
                        new Transformation(worldToScreen),
                        featureAdapter,
                        renderablePolygonProvider,
                        textSizeProvider,
                        svgProvider,
                        onePixelX(screenToWorld),
                        onePixelY(screenToWorld));

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
            graphics.setTransform(worldToScreen);

            for (var map : maps) {
                System.out.println("Map # = " + whichMap);
                reversedMaps.addFirst(map);
                var textInfo =
                        renderGraphics(graphicsRenderingContext, map.layers(), viewPortBounds);
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
                        graphicsRenderingContext, map.layers(), viewPortBounds, layerMap.get(map));
                whichMap--;
            }
            System.out.println("Text time = " + (System.currentTimeMillis() - tStart));

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        System.out.println("Rendering time " + (System.currentTimeMillis() - start));
        System.out.println("Total features " + featureCount);
    }

    private HashMap<IMapLayer<F, E>, TextInfo> renderGraphics(
            RenderingContext<F> context,
            Collection<? extends IMapLayer<F, E>> layers,
            E viewPortBounds)
            throws Exception {

        var layerTextInfo = new HashMap<IMapLayer<F, E>, TextInfo>();

        for (var layer : layers) {
            if (layer.isVisible()) {
                var appliedRule = new boolean[layer.style().rules().size()];
                var layerNeedsTextLayout = false;
                var style = layer.style();
                try (var iterator = layer.featureSource().features(viewPortBounds, dScale)) {
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
            RenderingContext<F> context,
            Collection<? extends IMapLayer<F, E>> layers,
            E viewPortBounds,
            HashMap<IMapLayer<F, E>, TextInfo> layerMap)
            throws Exception {

        for (var layer : layers) {
            if (layer.isVisible()) {
                var textInfo = layerMap.get(layer);
                if (textInfo.layerNeedsTextLayout()) {
                    try (var iterator = layer.featureSource().features(viewPortBounds, dScale)) {
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
            RenderingContext<F> context,
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

    private boolean applyGraphicsRule(Rule<F> rule, RenderingContext<F> context, F feature) {

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

    private void applyTextRule(Rule<F> rule, RenderingContext<F> context, F feature) {
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