/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author graham
 */
public class SVGCache {

	private final Map<Pair<String, Integer>, Image> cache = new HashMap<>();

	public void cache(String svg, int size, Image image) {
		cache.put(Pair.of(svg, size), image);
	}

	public Image fetch(String svg, int size) {
		return cache.get(Pair.of(svg, size));
	}

	public int size() {
		return cache.size();
	}

}
