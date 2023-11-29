/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.symbolizer;

import javafx.scene.paint.Color;
import org.geotools.api.feature.simple.SimpleFeature;
import org.knowtiphy.shapemap.renderer.RenderingContext;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.BaseMarkSymbolizer;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 */
public class SVGPathSymbolizer extends BaseMarkSymbolizer {

	public SVGPathSymbolizer(FillInfo fillInfo, StrokeInfo strokeInfo) {
		super(fillInfo, strokeInfo);
	}

	// private String dave = "M%d,%dh24v24z";

	private String dave = "M%d %d h24v24z";// M12 3l2 3l2 15h-8l2-15zL8,9,16,9M3 11l2
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

	@Override
	public void render(RenderingContext context, SimpleFeature feature, Point pt, PointSymbolizer pointSymbolizer) {

		var gc = context.graphicsContext();
		var tx = context.worldToScreen();

		var x = pt.getX();
		var y = pt.getY();
		System.err.println(dave.formatted((int) x, (int) y));
		gc.setLineWidth(1);
		// gc.setFill(Color.RED);
		gc.setStroke(Color.BLACK);
		gc.appendSVGPath(dave.formatted((int) x, (int) y));
		// gc.closePath();
		// gc.fill();
		gc.stroke();
	}

}