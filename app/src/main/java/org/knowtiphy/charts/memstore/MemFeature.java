/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.knowtiphy.shapemap.api.GeomType;
import org.locationtech.jts.geom.Geometry;

import static org.locationtech.jts.geom.Geometry.TYPENAME_GEOMETRYCOLLECTION;
import static org.locationtech.jts.geom.Geometry.TYPENAME_LINEARRING;
import static org.locationtech.jts.geom.Geometry.TYPENAME_LINESTRING;
import static org.locationtech.jts.geom.Geometry.TYPENAME_MULTILINESTRING;
import static org.locationtech.jts.geom.Geometry.TYPENAME_MULTIPOINT;
import static org.locationtech.jts.geom.Geometry.TYPENAME_MULTIPOLYGON;
import static org.locationtech.jts.geom.Geometry.TYPENAME_POINT;
import static org.locationtech.jts.geom.Geometry.TYPENAME_POLYGON;

/**
 * A feature in an in memory feature store
 */

public class MemFeature extends SimpleFeatureImpl
{

  private final GeomType geomType;

  private final Geometry defaultGeometry;

  public MemFeature(SimpleFeature geoFeature)
  {
    super(geoFeature.getAttributes(), geoFeature.getFeatureType(), geoFeature.getIdentifier());
    this.defaultGeometry = (Geometry) geoFeature.getDefaultGeometry();
    this.geomType = geomType(defaultGeometry);
  }

  public GeomType geomType()
  {
    return geomType;
  }

  public Geometry defaultGeometry()
  {
    return defaultGeometry;
  }

  private static GeomType geomType(Geometry geom)
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