/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.symbolizer.mark;

import shapemap.renderer.GraphicsRenderingContext;
import shapemap.renderer.graphics.Stroke;
import shapemap.renderer.symbolizer.PointSymbolizer;
import shapemap.renderer.symbolizer.basic.FillInfo;
import shapemap.renderer.api.IFeature;
import shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 */
public class CrossMarkSymbolizer<F extends IFeature> extends BaseMarkSymbolizer<F> {

	public CrossMarkSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
		super(fillInfo, strokeInfo);
	}

	@Override
	public void render(GraphicsRenderingContext context, F feature, Point pt, PointSymbolizer<F> pointSymbolizer) {

		var szo = pointSymbolizer.size().apply(feature, pt);
		if (szo == null)
			return;

		var x = pt.getX();
		var y = pt.getY();
		var sz = szo.doubleValue();

		var sizeX = sz * context.onePixelX();
		var sizeY = sz * context.onePixelY();
		var halfSizeX = sizeX / 2;
		var halfSizeY = sizeY / 2;

		// TODO -- what does filling a cross mean?
		// if (fillInfo != null) {
		// Fill.setup(context, fillInfo);
		// gc.fillRect(tx.getX() - halfSizeX, tx.getY() - halfSizeY, sizeX, sizeY);
		// }

		var gc = context.graphicsContext();

		if (strokeInfo != null) {
			Stroke.setup(context, strokeInfo);
			gc.strokeLine(x - halfSizeX, y, x + halfSizeX, y);
			gc.strokeLine(x, y - halfSizeY, x, y + halfSizeY);
		}
	}

}