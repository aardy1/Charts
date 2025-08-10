/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.shapemap.renderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import org.knowtiphy.shapemap.api.RenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Rule;

/**
 * @author graham
 */
public class ShapeMapRenderer<S, F, E> {

    private int featureCount = 0;

    private final RenderingContext<S, F, E> renderingContext;

    private final GraphicsContext graphics;

    public ShapeMapRenderer(RenderingContext<S, F, E> renderingContext, GraphicsContext graphics) {
        this.renderingContext = renderingContext;
        this.graphics = graphics;
    }

    public void paint() {

        featureCount = 0;
        var start = System.currentTimeMillis();

        //        var index = new Quadtree();

        var graphicsRenderingContext =
                new GraphicsRenderingContext<>(
                        renderingContext,
                        graphics,
                        new Transformation(renderingContext.worldToScreen()),
                        onePixelX(renderingContext.screenToWorld()),
                        onePixelY(renderingContext.screenToWorld()));

        try {
            // pass 1 -- do graphics -- point, line and polygon symbolizers
            // We keep track of:
            // a) which rules were applied
            // b) which layers need text layout (had rules that were applied and have text
            // symbolizers

            var appliedRule = new boolean[renderingContext.totalRuleCount()];
            var layerNeedsTextLayout = new boolean[renderingContext.layers().size()];

            var gStart = System.currentTimeMillis();
            graphics.setTransform(renderingContext.worldToScreen());
            renderGraphics(graphicsRenderingContext, appliedRule, layerNeedsTextLayout);
            System.err.println("Graphics time = " + (System.currentTimeMillis() - gStart));

            // pass 2 -- render text using the information computed in pass 1
            var tStart = System.currentTimeMillis();
            graphics.setTransform(Transformation.IDENTITY);
            renderText(graphicsRenderingContext, appliedRule, layerNeedsTextLayout);
            System.err.println("Text time = " + (System.currentTimeMillis() - tStart));

            System.err.println("Rendering time " + (System.currentTimeMillis() - start));
            System.err.println("\n\n\n");
        } catch (Exception ex) {
            System.err.println("Rendering exception");
            ex.printStackTrace(System.err);
        }

        System.err.println("Total features " + featureCount);
    }

    private void renderGraphics(
            GraphicsRenderingContext<S, F, E> context,
            boolean[] appliedRule,
            boolean[] layerNeedsTextLayout)
            throws Exception {

        var layers = renderingContext.layers();
        var viewPortBounds = renderingContext.viewPortBounds();

        var layerPos = 0;
        var rulePos = 0;

        for (var layer : layers) {
            if (layer.isVisible()) {
                var style = layer.style();

                try (var iterator =
                        layer.featureSource()
                                .features(
                                        viewPortBounds,
                                        renderingContext.displayScale(),
                                        layer.isScaleLess())) {
                    for (var feature : iterator) {
                        featureCount++;
                        //                        var feature = iterator.next();
                        layerNeedsTextLayout[layerPos] |=
                                applyStyle(style, context, feature, appliedRule, rulePos);
                    }
                }

                layerNeedsTextLayout[layerPos] &= style.hasTextSymbolizers();
            }

            layerPos++;
            rulePos += layer.style().rules().size();
        }
    }

    private void renderText(
            GraphicsRenderingContext<S, F, E> context,
            boolean[] appliedRule,
            boolean[] layerNeedsTextLayout)
            throws Exception {

        var layers = renderingContext.layers();
        var viewPortBounds = renderingContext.viewPortBounds();

        var layerPos = 0;
        var rulePos = 0;

        for (var layer : layers) {
            if (layerNeedsTextLayout[layerPos]) {
                try (var iterator =
                        layer.featureSource()
                                .features(viewPortBounds, renderingContext.displayScale())) {
                    for (var feature : iterator) {
                        var rp = rulePos;
                        for (var rule : layer.style().rules()) {
                            if (appliedRule[rp]) {
                                applyTextRule(rule, context, feature);
                            }

                            rp++;
                        }
                    }
                }
            }

            layerPos++;
            rulePos += layer.style().rules().size();
        }
    }

    private boolean applyStyle(
            FeatureTypeStyle<S, F> style,
            GraphicsRenderingContext<S, F, E> context,
            F feature,
            boolean[] appliedRule,
            int startPos) {

        var appliedSomeRule = false;
        // if (style.applies(feature))
        var rulePos = startPos;
        var elsePos = -1;

        for (var rule : style.rules()) {
            if (!rule.elseFilter()) {
                var applied = applyGraphicsRule(rule, context, feature);
                appliedRule[rulePos] |= applied;
                appliedSomeRule |= applied;
            } else {
                elsePos = rulePos - startPos;
            }

            rulePos++;
        }

        if (!appliedSomeRule && elsePos != -1) {
            var elseRule = style.rules().get(elsePos);
            var applied = applyGraphicsRule(elseRule, context, feature);
            appliedRule[elsePos] |= applied;
            appliedSomeRule = applied;
        }

        return appliedSomeRule;
    }

    private boolean applyGraphicsRule(
            Rule<S, F> rule, GraphicsRenderingContext<S, F, E> context, F feature) {
        var featureAdapter = context.renderingContext().featureAdapter();
        if (rule.filter() != null
                && rule.filter().apply(feature, featureAdapter.defaultGeometry(feature))) {
            for (var symbolizer : rule.graphicSymbolizers()) {
                symbolizer.render(context, feature);
            }

            return true;
        }

        return false;
    }

    private void applyTextRule(
            Rule<S, F> rule, GraphicsRenderingContext<S, F, E> context, F feature) {
        var featureAdapter = context.renderingContext().featureAdapter();
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

// System.err.println("Num text rules = " + textRules.size());
// for (var entry : textRules.entrySet()) {
// try (var iterator = entry.getKey().getFeatureSource().getFeatures().features())
// {
// while (iterator.hasNext()) {
// var feature = (SimpleFeature) iterator.next();
// for (var rule : entry.getValue()) {
// // System.err.println(rule + " : " + entry.getValue().size());
// for (var symbolizer : rule.getTextSymbolizers())
// symbolizer.render(context, feature);
// }
// }
// }
    // }