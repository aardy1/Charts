/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.context;

import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author graham
 */
public class RenderGeomCache
{

  private final Map<Geometry, Geometry> cache = new HashMap<>();

  public void put(Geometry key, Geometry value)
  {
    cache.put(key, value);
  }

  public Geometry get(Geometry key)
  {
    return cache.get(key);
  }

}