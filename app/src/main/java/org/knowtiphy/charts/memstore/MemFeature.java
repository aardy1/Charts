/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import org.geotools.api.feature.simple.*;
import org.geotools.feature.simple.*;
import org.knowtiphy.shapemap.api.*;
import org.locationtech.jts.geom.*;

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
    this.geomType = ExtraAttributes.geomType(defaultGeometry);
  }

  public GeomType geomType()
  {
    return geomType;
  }

  public Geometry defaultGeometry()
  {
    return defaultGeometry;
  }

}