/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.symbolizer.mark;

import shapemap.renderer.GraphicsRenderingContext;
import shapemap.renderer.graphics.Fill;
import shapemap.renderer.graphics.Stroke;
import shapemap.renderer.symbolizer.PointSymbolizer;
import shapemap.renderer.symbolizer.basic.FillInfo;
import shapemap.renderer.api.IFeature;
import shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 */
public class TriangleMarkSymbolizer<F extends IFeature> extends BaseMarkSymbolizer<F> {

	public TriangleMarkSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
		super(fillInfo, strokeInfo);
	}

	// TODO -- this is hacky
	private static final double[] xs = new double[4];

	private static final double[] ys = new double[4];

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

		xs[0] = x;
		ys[0] = y + halfSizeY;
		xs[1] = x - halfSizeX;
		ys[1] = y - halfSizeY;
		xs[2] = x + halfSizeX;
		ys[2] = y - halfSizeY;

		var gc = context.graphicsContext();
		if (fillInfo != null) {
			Fill.setup(context, fillInfo);
			gc.fillPolygon(xs, ys, 3);
		}

		if (strokeInfo != null) {
			Stroke.setup(context, strokeInfo);
			xs[3] = x;
			ys[3] = y + halfSizeY;
			gc.strokePolyline(xs, ys, 4);
		}
	}

}