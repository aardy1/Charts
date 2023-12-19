/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.shapemap.renderer.context.RendererContext;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Rule;
import org.locationtech.jts.index.quadtree.Quadtree;

/**
 * @author graham
 */
public class ShapeMapRenderer<S, F>
{

  private final RendererContext<S, F> rendererContext;

  private final GraphicsContext graphics;

  public static IntegerProperty count = new SimpleIntegerProperty(0);

  public ShapeMapRenderer(RendererContext<S, F> rendererContext, GraphicsContext graphics)
  {
    this.rendererContext = rendererContext;
    this.graphics = graphics;
  }

  public void paint() throws TransformException, NonInvertibleTransformException
  {

    var layers = rendererContext.layers();
    var viewPortBounds = rendererContext.viewPortBounds();

    // TODO -- get rid of this debugging code
    count.set(count.get() + 1);

    System.err.println("\nRepaint : " + count.get() + " : " + rendererContext.paintArea() + "\n");

    var start = System.currentTimeMillis();

    var worldToScreen = RendererUtilities.worldToScreenTransform(viewPortBounds,
      rendererContext.paintArea(), viewPortBounds.getCoordinateReferenceSystem());
    var screenToWorld = worldToScreen.createInverse();

    var onePixelX = onePixelX(screenToWorld);
    var onePixelY = onePixelY(screenToWorld);

    var index = new Quadtree();
    var graphicsRenderingContext = new GraphicsRenderingContext<>(rendererContext, graphics,
      new Transformation(worldToScreen), onePixelX, onePixelY, index, viewPortBounds);

    try
    {
      // pass 1 -- do graphics -- point, line and polygon symbolizers
      // We keep track of:
      // a) which rules were applied
      // b) which layers need text layout (had rules that were applied and have text
      // symbolizers

      var appliedRule = new boolean[rendererContext.totalRuleCount()];
      var layerNeedsTextLayout = new boolean[layers.size()];

      var gStart = System.currentTimeMillis();
      graphics.setTransform(worldToScreen);
      renderGraphics(graphicsRenderingContext, appliedRule, layerNeedsTextLayout);
      System.err.println("Graphics time = " + (System.currentTimeMillis() - gStart));

      // pass 2 -- render text using the information computed in pass 1

      var tStart = System.currentTimeMillis();
      graphics.setTransform(Transformation.IDENTITY);
      renderText(graphicsRenderingContext, appliedRule, layerNeedsTextLayout);
      System.err.println("Text time = " + (System.currentTimeMillis() - tStart));

      System.err.println("Rendering time " + (System.currentTimeMillis() - start));
      System.err.println("\n\n\n");
    }
    catch(Exception ex)
    {
      System.err.println("Rendering exception");
      ex.printStackTrace(System.err);
    }
  }

  private void renderGraphics(
    GraphicsRenderingContext<S, F> context, boolean[] appliedRule, boolean[] layerNeedsTextLayout)
    throws Exception
  {

    var layers = rendererContext.layers();
    var viewPortBounds = rendererContext.viewPortBounds();

    var layerPos = 0;
    var rulePos = 0;

    for(var layer : layers)
    {
      if(layer.isVisible())
      {
        var style = layer.getStyle();

        try(var iterator = layer.getFeatures(viewPortBounds, layer.isScaleLess()))
        {
          while(iterator.hasNext())
          {
            var feature = iterator.next();
            layerNeedsTextLayout[layerPos] |= applyStyle(style, context, feature, appliedRule,
              rulePos);
          }
        }

        layerNeedsTextLayout[layerPos] &= style.hasTextSymbolizers();
      }

      layerPos++;
      rulePos += layer.getStyle().rules().size();
    }
  }

  private void renderText(
    GraphicsRenderingContext<S, F> context, boolean[] appliedRule, boolean[] layerNeedsTextLayout)
    throws Exception
  {

    var layers = rendererContext.layers();
    var viewPortBounds = rendererContext.viewPortBounds();

    var layerPos = 0;
    var rulePos = 0;

    for(var layer : layers)
    {
      if(layerNeedsTextLayout[layerPos])
      {
        try(var iterator = layer.getFeatures(viewPortBounds, true))
        {
          while(iterator.hasNext())
          {
            var feature = iterator.next();
            var rp = rulePos;
            for(var rule : layer.getStyle().rules())
            {
              if(appliedRule[rp])
              {
                applyTextRule(rule, context, feature);
              }

              rp++;
            }
          }
        }
      }

      layerPos++;
      rulePos += layer.getStyle().rules().size();
    }

  }

  private boolean applyStyle(
    FeatureTypeStyle<S, F> style, GraphicsRenderingContext<S, F> context, F feature,
    boolean[] appliedRule, int startPos)
  {

    var appliedSomeRule = false;
    // if (style.applies(feature))
    {

      var rulePos = startPos;
      var elsePos = -1;

      for(var rule : style.rules())
      {
        if(!rule.elseFilter())
        {
          var applied = applyGraphicsRule(rule, context, feature);
          appliedRule[rulePos] |= applied;
          appliedSomeRule |= applied;
        }
        else
        {
          elsePos = rulePos - startPos;
        }

        rulePos++;
      }

      if(!appliedSomeRule && elsePos != -1)
      {
        var elseRule = style.rules().get(elsePos);
        var applied = applyGraphicsRule(elseRule, context, feature);
        appliedRule[elsePos] |= applied;
        appliedSomeRule |= applied;
      }
    }

    return appliedSomeRule;
  }

  private boolean applyGraphicsRule(
    Rule<S, F> rule, GraphicsRenderingContext<S, F> context, F feature)
  {

    var featureAdapter = context.rendererContext().featureAdapter();
    if(rule.filter() != null)
    {
      if(rule.filter().apply(feature, featureAdapter.defaultGeometry(feature)))
      {
        for(var symbolizer : rule.graphicSymbolizers())
        {
          symbolizer.render(context, feature);
        }

        return true;
      }
    }

    return false;
  }

  private void applyTextRule(Rule<S, F> rule, GraphicsRenderingContext<S, F> context, F feature)
  {

    var featureAdapter = context.rendererContext().featureAdapter();
    if(rule.filter().apply(feature, featureAdapter.defaultGeometry(feature)))
    {
      for(var symbolizer : rule.textSymbolizers())
      {
        symbolizer.render(context, feature);
      }
    }
  }

  private double onePixelX(Affine screenToWorld) throws NonInvertibleTransformException
  {
    var pt1 = screenToWorld.transform(0, 0);
    var pt2 = screenToWorld.transform(1, 0);
    return Math.abs(pt2.getX() - pt1.getX());
  }

  private double onePixelY(Affine screenToWorld) throws NonInvertibleTransformException
  {
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