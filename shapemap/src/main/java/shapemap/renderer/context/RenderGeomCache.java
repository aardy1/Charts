/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.context;

import java.util.HashMap;
import java.util.Map;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class RenderGeomCache {

	private final Map<Geometry, Geometry> cache = new HashMap<>();

	public void cache(Geometry key, Geometry value) {
		cache.put(key, value);
	}

	public Geometry fetch(Geometry key) {
		return cache.get(key);
	}

}
