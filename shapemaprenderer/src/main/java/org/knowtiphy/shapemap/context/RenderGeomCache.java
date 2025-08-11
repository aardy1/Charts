/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.context;

import java.util.HashMap;
import java.util.function.Function;
import org.locationtech.jts.geom.Polygon;

/** A simple cache of rendering geometries.. */
public class RenderGeomCache {

    public RenderGeomCache() {}

    private final HashMap<Polygon, Polygon> cache = new HashMap<>();

    public Polygon computeIfAbsent(Polygon key, Function<Polygon, Polygon> func) {
        return cache.computeIfAbsent(key, func);
    }
}