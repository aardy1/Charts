/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import org.knowtiphy.shapemap.api.*;
import org.locationtech.jts.geom.*;

import static org.locationtech.jts.geom.Geometry.*;

/**
 * @author graham
 */
public class ExtraAttributes
{
  public static GeomType geomType(Geometry geom)
  {
    return switch(geom.getGeometryType())
    {
      case TYPENAME_POINT -> GeomType.POINT;
      case TYPENAME_MULTIPOINT -> GeomType.MULTI_POINT;
      case TYPENAME_LINESTRING -> GeomType.LINE_STRING;
      case TYPENAME_LINEARRING -> GeomType.LINEAR_RING;
      case TYPENAME_MULTILINESTRING -> GeomType.MULTI_LINE_STRING;
      case TYPENAME_POLYGON -> GeomType.POLYGON;
      case TYPENAME_MULTIPOLYGON -> GeomType.MULTI_POLYGON;
      case TYPENAME_GEOMETRYCOLLECTION -> GeomType.GEOMETRY_COLLECTION;
      default -> throw new IllegalArgumentException(geom.getGeometryType());
    };
  }
}