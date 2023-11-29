/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.context;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.tuple.Pair;
import org.girod.javafx.svgimage.SVGLoader;
import org.knowtiphy.charts.chartview.ChartView;

/**
 * @author graham
 */
public class SVGCache implements ISVGProvider {

	private static final SnapshotParameters SVG_RENDERING_PARAMETERS = new SnapshotParameters();
	static {
		SVG_RENDERING_PARAMETERS.setFill(Color.TRANSPARENT);
	}

	//@formatter:on
	private final Map<Pair<String, Integer>, Image> cache = new HashMap<>();

	public void cache(String svg, int size, Image image) {
		cache.put(Pair.of(svg, size), image);
	}

	@Override
	public Image fetch(String name, int size) {
		var image = cache.get(Pair.of(name, size));
		if (image == null) {
			var svgImage = SVGLoader.load(ChartView.class.getResource("markicons/" + name));
			image = svgImage.toImage(SVG_RENDERING_PARAMETERS);
			cache(name, size, image);
		}

		return image;
	}

}
