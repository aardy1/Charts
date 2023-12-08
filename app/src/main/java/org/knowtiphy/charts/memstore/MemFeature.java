/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import org.geotools.api.feature.simple.*;
import org.geotools.api.filter.identity.*;
import org.geotools.feature.simple.*;
import org.knowtiphy.shapemap.api.*;
import org.locationtech.jts.geom.*;

import java.util.*;

/**
 * @author graham
 */
public class MemFeature extends SimpleFeatureImpl
{

  private final GeomType geomType;

  private final Geometry defaultGeometry;

  public MemFeature(
    List<Object> values, SimpleFeatureType featureType, Geometry defaultGeometry, FeatureId id)
  {
    super(values, featureType, id);
    this.defaultGeometry = defaultGeometry;
    this.geomType = ExtraAttributes.geomType(defaultGeometry);
  }

  public GeomType geomType()
  {
    return geomType;
  }

  public Geometry defaultGeometry()
  {
    return defaultGeometry;//(Geometry) super.getDefaultGeometry();
  }

}