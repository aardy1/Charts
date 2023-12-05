/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer.mark;

import org.knowtiphy.shapemap.renderer.GraphicsRenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.PointSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.PathInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 */
public class SVGMarkSymbolizer<F extends IFeature> extends BaseMarkSymbolizer<F> {

	private final PathInfo pathInfo;

	public SVGMarkSymbolizer(PathInfo pathInfo, FillInfo fillInfo, StrokeInfo strokeInfo) {
		super(fillInfo, strokeInfo);
		this.pathInfo = pathInfo;
	}

	@Override
	public void render(GraphicsRenderingContext context, F feature, Point pt, PointSymbolizer<F> pointSymbolizer) {

		var szo = pointSymbolizer.size().apply(feature, pt);
		if (szo == null)
			return;

		var rotationO = pointSymbolizer.rotation() == null ? null : pointSymbolizer.rotation().apply(feature, pt);
		var rotation = rotationO == null ? 0 : rotationO.doubleValue();
		var image = context.rendererContext().svgProvider().get(pathInfo.name(), szo.intValue(), rotation);

		var x = pt.getX();
		var y = pt.getY();
		var sizeX = image.getWidth() * context.onePixelX();
		var sizeY = image.getHeight() * context.onePixelY();
		var halfSizeX = sizeX / 2;
		var halfSizeY = sizeY / 2;

		// TODO -- make the image fetcher into a feature function of some sort and put the
		// provider in there
		context.graphicsContext().drawImage(image, x - halfSizeX, y - halfSizeY, sizeX, sizeY);
	}

}

// private void appendSVG(GraphicsContext gc, String path) {
// gc.save();
// gc.setTransform(Transformation.IDENTITY);
// gc.setFill(Color.RED);
// gc.setStroke(Color.BLACK);
// gc.setLineWidth(1);
// gc.beginPath();
// gc.appendSVGPath(path);
// // gc.fill();
// gc.stroke();
// gc.restore();
// }

// appendSVG(gc, DAVE.formatted((int) x, (int) y));
// appendSVG(gc,"M 50 50 L 150 50 L 100 150 z");// M 200 200 L 700 200 L 300 500
// z");
// appendSVG(gc,
// "M %d %d c15-2 139-52 169-63 13-5 44-19 56-20 4 34-2 69 6 101-12 9-26 18-40
// 27-12 9-29 18-39 26v43c22-5 93-41 107-41 13 0 85 36 108 40 0-51
// 8-37-40-68-11-7-29-22-39-26 3-47 3-59 5-103 20 4 203 82 225
// 84v-53l-57-44c-18-16-37-29-55-44l-113-88c-8-15 8-121-10-158-6-12-11-28-28-27-13
// 8-25 33-27 52-2 21-2 88-2 115 1 19 0 17-11 26L641 997v53z"
// .formatted((int) x, (int) y));

// appendSVG(gc, DAVE.formatted((int) x, (int) y));
// appendSVG(gc,"M 50 50 L 150 50 L 100 150 z");// M 200 200 L 700 200 L 300 500
// z");
// appendSVG(gc,
// "M %d %d c15-2 139-52 169-63 13-5 44-19 56-20 4 34-2 69 6 101-12 9-26 18-40
// 27-12 9-29 18-39 26v43c22-5 93-41 107-41 13 0 85 36 108 40 0-51
// 8-37-40-68-11-7-29-22-39-26 3-47 3-59 5-103 20 4 203 82 225
// 84v-53l-57-44c-18-16-37-29-55-44l-113-88c-8-15 8-121-10-158-6-12-11-28-28-27-13
// 8-25 33-27 52-2 21-2 88-2 115 1 19 0 17-11 26L641 997v53z"
// .formatted((int) x, (int) y));

// drawImage(context, img, x, y, 12);

// private String dave = "M%d,%dh24v24z";

// private String dave = "M%d %d h24v24z";// M12 3l2 3l2 15h-8l2-15zL8,9,16,9M3 11l2
// -2l-2 -2M21 11l-2 -2l2 -2";

// <path stroke="red" d="M0 0h24v24H0z" fill="none"/> <path d="M12 3l2 3l2 15h-8l2
// -15z" />
// <line x1="8" y1="9" x2="16" y2="9" /> <path d="M3 11l2 -2l-2 -2" />
// <path d="M21 11l-2 -2l2 -2" /> </svg>
// private String dave = "M24v24H0zM12 3l2 3l2 15h-8l2-15zL8,9,16,9M3 11l2 -2l-2 -2M21
// 11l-2 -2l2 -2";

// M23.6,0c-3.4,0-6.3,2.7-7.6,5.6C14.7,2.7,11.8,0,8.4,0C3.8,0,0,3.8,0,8.4c0,9.4,9.5,11.9,16,21.2
// c6.1-9.3,16-12.1,16-21.2C32,3.8,28.2,0,23.6,0z

// <svg xmlns="http://www.w3.org/2000/svg"
// width="240"
// height="240"
// viewBox="0 0 240 240"
// stroke-width="2" stroke="red" fill="none"
// stroke-linecap="round" stroke-linejoin="round">

// private static String DAVE = """
// M %d %d c15-2 139-52 169-63 13-5 44-19 56-20 4 34-2 69 6 101-12 9-26 18-40
// 27-12 9-29 18-39 26v43c22-5 93-41 107-41 13 0 85 36 108 40 0-51
// 8-37-40-68-11-7-29-22-39-26 3-47 3-59 5-103 20 4 203 82 225
// 84v-53l-57-44c-18-16-37-29-55-44l-113-88c-8-15 8-121-10-158-6-12-11-28-28-27-13
// 8-25 33-27 52-2 21-2 88-2 115 1 19 0 17-11 26L641 997v53z
// """;

//