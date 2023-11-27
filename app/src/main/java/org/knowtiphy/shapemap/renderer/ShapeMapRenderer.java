/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer;

import java.io.IOException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.memstore.MemStoreQuery;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Rule;
import org.knowtiphy.shapemap.viewmodel.MapViewModel;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.index.quadtree.Quadtree;

/**
 * @author graham
 */
public class ShapeMapRenderer {

	private final MapViewModel map;

	private final GraphicsContext graphics;

	public static IntegerProperty count = new SimpleIntegerProperty(0);

	public ShapeMapRenderer(MapViewModel map, GraphicsContext graphics) {
		this.map = map;
		this.graphics = graphics;
	}

	public void paint(Rectangle2D paintArea, ReferencedEnvelope viewportBounds)
			throws TransformException, IOException, NonInvertibleTransformException, FactoryException {

		System.err.println("\nRepaint : " + paintArea + "\n");

		count.set(count.get() + 1);
		var start = System.currentTimeMillis();

		var worldToScreen = RendererUtilities.worldToScreenTransform(viewportBounds, paintArea, map.crs());
		var index = new Quadtree();

		var screenToWorld = worldToScreen.createInverse();
		var pt1 = screenToWorld.transform(0, 0);
		var pt2 = screenToWorld.transform(1, 0);
		var onePixelX = Math.abs(pt2.getX() - pt1.getX());
		pt1 = screenToWorld.transform(0, 0);
		pt2 = screenToWorld.transform(0, 1);
		var onePixelY = Math.abs(pt2.getY() - pt1.getY());

		graphics.save();
		graphics.setTransform(worldToScreen);
		var context = new RenderingContext(graphics, new Transformation(worldToScreen), onePixelX, onePixelY,
				map.renderGeomCache(), index, viewportBounds);

		// pass 1 -- do graphics -- point, line and polygon symbolizers
		// We keep track of:
		// a) which rules were applied
		// b) which layers need text layour (so had rules that were applied and have text
		// symbolizers)

		try {
			var gStart = System.currentTimeMillis();

			var appliedRule = new boolean[map.getTotalRuleCount()];
			var layerNeedsTextLayout = new boolean[map.layers().size()];

			var layerPos = 0;
			var rulePos = 0;

			for (var layer : map.layers()) {
				if (layer.isVisible()) {
					var style = layer.getStyle();

					// TODO -- fix this
					try (var iterator = layer.getFeatureSource()
							.getFeatures(new MemStoreQuery(map.viewPortBounds(), layer.isScaleLess())).features()) {
						while (iterator.hasNext()) {
							var feature = (SimpleFeature) iterator.next();
							layerNeedsTextLayout[layerPos] |= applyStyle(style, context, feature, appliedRule, rulePos);
						}
					}

					layerNeedsTextLayout[layerPos] &= style.hasTextSymbolizers();
				}
				layerPos++;
				rulePos += layer.getStyle().rules().size();
			}

			System.err.println("Graphics time = " + (System.currentTimeMillis() - gStart));

			var tStart = System.currentTimeMillis();

			layerPos = 0;
			rulePos = 0;

			graphics.restore();

			for (var layer : map.layers()) {
				System.err.println("Layer " + layer.title() + " vis = " + layer.isVisible());
				if (layerNeedsTextLayout[layerPos]) {
					// TODO - fix this
					try (var iterator = layer.getFeatureSource()
							.getFeatures(new MemStoreQuery(map.viewPortBounds(), true)).features()) {
						while (iterator.hasNext()) {
							var feature = (SimpleFeature) iterator.next();
							var rp = rulePos;
							for (var rule : layer.getStyle().rules()) {
								if (appliedRule[rp]) {
									applyTextRule(rule, context, feature, index);
								}

								rp++;
							}
						}
					}
				}

				layerPos++;
				rulePos += layer.getStyle().rules().size();
			}

			System.err.println("Text time = " + (System.currentTimeMillis() - tStart));

			System.err.println("Rendering time " + (System.currentTimeMillis() - start));
			System.err.println("\n\n\n");
		}
		catch (Exception ex) {
			System.err.println("Rendering exception");
			ex.printStackTrace(System.err);
		}
	}

	private boolean applyStyle(FeatureTypeStyle style, RenderingContext context, SimpleFeature feature,
			boolean[] appliedRule, int startPos) {

		var appliedSomeRule = false;
		if (style.applies(feature)) {

			var rulePos = startPos;
			var elsePos = -1;

			for (var rule : style.rules()) {
				if (!rule.elseFilter()) {
					var applied = applyGraphicsRule(rule, context, feature);
					appliedRule[rulePos] |= applied;
					appliedSomeRule |= applied;
				}
				else
					elsePos = rulePos - startPos;

				rulePos++;
			}

			if (!appliedSomeRule && elsePos != -1) {
				var elseRule = style.rules().get(elsePos);
				var applied = applyGraphicsRule(elseRule, context, feature);
				appliedRule[elsePos] |= applied;
				appliedSomeRule |= applied;
			}
		}

		return appliedSomeRule;
	}

	private boolean applyGraphicsRule(Rule rule, RenderingContext context, SimpleFeature feature) {

		if (rule.filter() != null) {
			var filterResult = rule.filter().apply(feature, (Geometry) feature.getDefaultGeometry());
			if (filterResult instanceof Boolean fr && fr) {
				for (var symbolizer : rule.graphicSymbolizers()) {
					symbolizer.render(context, feature);
				}

				return true;
			}
		}

		return false;
	}

	private void applyTextRule(Rule rule, RenderingContext context, SimpleFeature feature, Quadtree index) {

		var filterResult = rule.filter().apply(feature, (Geometry) feature.getDefaultGeometry());
		if (filterResult instanceof Boolean fr && fr) {
			for (var symbolizer : rule.textSymbolizers()) {
				symbolizer.render(context, feature);
			}
		}
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