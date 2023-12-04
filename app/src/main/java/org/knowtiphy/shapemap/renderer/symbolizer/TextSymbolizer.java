/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import javafx.scene.text.Font;
import org.apache.commons.lang3.StringUtils;
import org.geotools.api.geometry.BoundingBox;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.knowtiphy.shapemap.renderer.Fonts;
import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.graphics.Fill;
import org.knowtiphy.shapemap.renderer.graphics.Text;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.api.IFeature;
import org.knowtiphy.shapemap.renderer.api.IFeatureFunction;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.LabelPlacement;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.index.quadtree.Quadtree;

/**
 * @author graham
 */
public class TextSymbolizer<F extends IFeature> {

	private final IFeatureFunction<F, String> label;

	private final Font font;

	private final FillInfo fillInfo;

	private final StrokeInfo strokeInfo;

	private final LabelPlacement labelPlacement;

	public TextSymbolizer(IFeatureFunction<F, String> label, Font font, FillInfo fillInfo, StrokeInfo strokeInfo,
			LabelPlacement labelPlacement) {

		this.label = label;
		this.font = font;
		this.fillInfo = fillInfo;
		this.strokeInfo = strokeInfo;
		this.labelPlacement = labelPlacement;
	}

	public void render(GraphicsRenderingContext context, F feature) {

		if (fillInfo != null) {
			Fill.setup(context, fillInfo);
		}

		if (strokeInfo != null) {
			Text.setup(context, strokeInfo);
		}

		if (fillInfo != null || strokeInfo != null) {
			context.graphicsContext().setFont(font);
		}

		text(context, feature, (Geometry) feature.getDefaultGeometry());
	}

	private void text(GraphicsRenderingContext context, F feature, Geometry geom) {

		// TODO -- switch on strings is brain dead
		switch (geom.getGeometryType()) {
			case Geometry.TYPENAME_POINT -> textPoint(context, feature, (Point) geom);
			case Geometry.TYPENAME_LINESTRING, Geometry.TYPENAME_LINEARRING ->
				textPoint(context, feature, ((LineString) geom).getStartPoint());
			case Geometry.TYPENAME_POLYGON -> textPoint(context, feature, ((Polygon) geom).getCentroid());
			case Geometry.TYPENAME_MULTIPOINT, Geometry.TYPENAME_MULTILINESTRING, Geometry.TYPENAME_MULTIPOLYGON ->
				recurse(context, feature, geom);
			default -> throw new IllegalArgumentException(geom.getGeometryType());
		}
	}

	private void textPoint(GraphicsRenderingContext context, F feature, Point point) {

		if (point != null && label != null) {
			var text = label.apply(feature, point);

			// TODO -- get rid of debugging
			// var bill =
			// feature.getFeatureType().getName().getLocalPart().contains("CURENT");

			if (!StringUtils.isBlank(text)) {

				var graphicsContext = context.graphicsContext();
				var tx = context.worldToScreen();
				var blocked = context.blocked();

				tx.apply(point.getX(), point.getY());
				// TODO -- this is wrong since it supposed to be from the bounding box
				// of the pt feature?

				var x = tx.getX() + (labelPlacement == null || labelPlacement.pointPlacement() == null ? 0
						: labelPlacement.pointPlacement().getDisplacementX());
				var y = tx.getY() + (labelPlacement == null || labelPlacement.pointPlacement() == null ? 0
						: labelPlacement.pointPlacement().getDisplacementY());

				var textDimensions = Fonts.textSizeFast(font, text);
				var textBounds = new ReferencedEnvelope(x, x + textDimensions.getWidth(), y,
						y + textDimensions.getHeight(), DefaultEngineeringCRS.CARTESIAN_2D);

				if (!overlaps(textBounds, blocked)) {

					if (fillInfo != null) {
						graphicsContext.fillText(text, x, y);
					}

					if (strokeInfo != null) {
						graphicsContext.strokeText(text, x, y);
					}

					// TODO -- set bounds from greater of fill or stroke
					blocked.insert(textBounds, textBounds);
				}
				// else if (bill)
				// System.err.println(text + " : " + " blocked");
			}
		}
	}

	// TODO -- text along line strings ...

	// only necessary if a multi-X, can contain another multi-X, rather than just X's
	private void recurse(GraphicsRenderingContext context, F feature, Geometry geom) {
		for (int i = 0; i < geom.getNumGeometries(); i++) {
			text(context, feature, geom.getGeometryN(i));
		}
	}

	// quadtree queries can gives false positives (can they,or is that just multi-X
	// related?) (so a query result of non empty does not necessarily imply overlaps)

	private boolean overlaps(ReferencedEnvelope bounds, Quadtree index) {

		for (var box : index.query(bounds)) {
			if (((BoundingBox) box).intersects(bounds)) {
				// System.err.println("Real intersection");
				return true;
			}
		}

		return false;
	}

}
// private void renderAt(RenderingContext context, SimpleFeature feature, Point point) {
//
// if (point != null && label != null) {
// var text = label.apply(feature, point);
// if (text != null) {
// var textString = text.toString();
// if (!StringUtils.isBlank(textString)) {
//
// var graphicsContext = context.graphicsContext();
// var tx = context.worldToScreen();
// var blocked = context.blocked();
//
// tx.reallyApply(point.getX(), point.getY());
// // TODO -- this is wrong since it supposed to be from the bounding box
// // of the pt feature?
//
// var x = tx.getX() + (labelPlacement == null || labelPlacement.pointPlacement() == null
// ? 0
// : labelPlacement.pointPlacement().getDisplacementX());
// var y = tx.getY() + (labelPlacement == null || labelPlacement.pointPlacement() == null
// ? 0
// : labelPlacement.pointPlacement().getDisplacementY());
//
// var textDimensions = Fonts.textSizeFast(font, textString);
// var textBounds = new ReferencedEnvelope(x, x + textDimensions.getWidth(), y,
// y + textDimensions.getHeight(), DefaultEngineeringCRS.CARTESIAN_2D);
//
// if (!overlaps(textBounds, blocked)) {
//
// if (fillInfo != null) {
//// Fill.setup(context, fillInfo);
//// graphicsContext.setFont(font);
// graphicsContext.fillText(textString, tx.getX(), tx.getY());
// }
//
// if (strokeInfo != null) {
//// Text.setup(context, font, strokeInfo);
// graphicsContext.strokeText(textString, tx.getX(), tx.getY());
// }
//
// // TODO -- set bounds from greater of fill or stroke
// blocked.insert(textBounds, textBounds);
// }
// }
// }
// }
// }
