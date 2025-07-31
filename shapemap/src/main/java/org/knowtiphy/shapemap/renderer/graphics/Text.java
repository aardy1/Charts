/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.graphics;

import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;

public class Text {
    /**
     * Setup text rendering values for a graphics context from stroke information
     *
     * @param context the rendering context
     * @param strokeInfo the stroke information
     */
    public static <S, F> void setup(GraphicsRenderingContext<S, F> context, StrokeInfo strokeInfo) {
        var gc = context.graphicsContext();
        gc.setStroke(strokeInfo.stroke());
        gc.setLineWidth(strokeInfo.strokeWidth());
        gc.setGlobalAlpha(strokeInfo.opacity());
    }
    //
    // public static void text(RenderingContext context, SimpleFeature feature, Geometry
    // geom) {
    //
    // switch (geom.getGeometryType()) {
    // case Geometry.TYPENAME_POINT -> textPoint(context, feature, (Point) geom);
    // case Geometry.TYPENAME_LINESTRING, Geometry.TYPENAME_LINEARRING ->
    // textPoint(context, feature, ((LineString) geom).getStartPoint());
    // case Geometry.TYPENAME_POLYGON -> textPoint(context, feature, ((Polygon)
    // geom).getCentroid());
    // case Geometry.TYPENAME_MULTIPOINT, Geometry.TYPENAME_MULTILINESTRING,
    // Geometry.TYPENAME_MULTIPOLYGON ->
    // recurse(context, feature, geom);
    // default -> throw new IllegalArgumentException(geom.getGeometryType());
    // }
    // }
    //
    // private static void textPoint(RenderingContext context, SimpleFeature feature,
    // Point point) {
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
    // var x = tx.getX() + (labelPlacement == null || labelPlacement.pointPlacement() ==
    // null ? 0
    // : labelPlacement.pointPlacement().getDisplacementX());
    // var y = tx.getY() + (labelPlacement == null || labelPlacement.pointPlacement() ==
    // null ? 0
    // : labelPlacement.pointPlacement().getDisplacementY());
    //
    // var textDimensions = Fonts.textSizeFast(font, textString);
    // var textBounds = new ReferencedEnvelope(x, x + textDimensions.getWidth(), y,
    // y + textDimensions.getHeight(), DefaultEngineeringCRS.CARTESIAN_2D);
    //
    // if (!overlaps(textBounds, blocked)) {
    //
    // if (fillInfo != null) {
    // Fill.setup(context, fillInfo);
    // graphicsContext.setFont(font);
    // graphicsContext.fillText(textString, tx.getX(), tx.getY());
    // }
    //
    // if (strokeInfo != null) {
    // Text.setup(context, font, strokeInfo);
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
    //
    // // only necessary if a multi-X, can contain another multi-X, rather than just X's
    // private static void recurse(RenderingContext context, SimpleFeature feature,
    // Geometry geom) {
    // for (int i = 0; i < geom.getNumGeometries(); i++) {
    // text(context, feature, geom.getGeometryN(i));
    // }
    // }
}