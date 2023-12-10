/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import org.geotools.api.feature.simple.SimpleFeatureType;
import org.knowtiphy.charts.UnitProfile;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.api.IFeatureFunction;
import org.knowtiphy.shapemap.api.IStyleCompilerAdapter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author graham
 */
public class ParsingContext implements IStyleCompilerAdapter<MemFeature>
{

  private static final Map<String, BiFunction<UnitProfile, IFeatureFunction<MemFeature, Object>,
                                               IFeatureFunction<MemFeature, Object>>> UNIT_MAP =
    new HashMap<>();

  static
  {
    UNIT_MAP.put("knotsToMapUnit", ParsingContext::knotsToMapUnits);
  }

  private final SimpleFeatureType featureType;

  private final UnitProfile unitProfile;

  public ParsingContext(SimpleFeatureType featureType, UnitProfile unitProfile)
  {
    this.featureType = featureType;
    this.unitProfile = unitProfile;
  }

  @Override
  public IFeatureFunction<MemFeature, Object> compilePropertyAccess(String name)
  {
    var index1 = featureType.indexOf(name);
    return (f, g) -> f.getAttribute(index1);
  }

  @Override
  public IFeatureFunction<MemFeature, Object> compileFunctionCall(
    String name, Collection<IFeatureFunction<MemFeature, Object>> args)
  {
    System.err.println("name = " + name);
    var function = UNIT_MAP.get(name);
    return function.apply(unitProfile, args.iterator().next());
  }

  private static IFeatureFunction<MemFeature, Object> knotsToMapUnits(
    UnitProfile unitProfile, IFeatureFunction<MemFeature, Object> quantity)
  {
    return (f, g) -> {
      var value = quantity.apply(f, g);
      return value == null ? null : unitProfile.fKnotsToMapUnits.apply(
        ((Number) value).doubleValue());
    };
  }
}