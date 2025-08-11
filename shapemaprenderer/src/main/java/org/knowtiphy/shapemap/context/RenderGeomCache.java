/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.context;

import java.util.HashMap;
import java.util.function.Function;
import org.knowtiphy.shapemap.api.Renderable;
import org.locationtech.jts.geom.Polygon;

/** A simple cache of rendering geometries.. */
public class RenderGeomCache {

    public RenderGeomCache() {}

    private final HashMap<Polygon, Renderable> cache = new HashMap<>();

    public Renderable computeIfAbsent(Polygon key, Function<Polygon, Renderable> func) {
        return cache.computeIfAbsent(key, func);
    }
}