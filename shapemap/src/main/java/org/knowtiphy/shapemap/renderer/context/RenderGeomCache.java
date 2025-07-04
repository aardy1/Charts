/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.context;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import java.util.HashMap;
import java.util.function.Function;

/**
 * @author graham
 */
public class RenderGeomCache
{
    private final HashMap<Polygon, Polygon> cache = new HashMap<>();

    public void put(Polygon key, Polygon value)
    {
        cache.put(key, value);
    }

    public Polygon computeIfAbsent(Polygon key, Function<Polygon, Polygon> func)
    {
        return cache.computeIfAbsent(key, func);
    }

    public Geometry get(Geometry key)
    {
        return cache.get(key);
    }

}