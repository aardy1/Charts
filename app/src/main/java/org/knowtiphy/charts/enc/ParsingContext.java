/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.knowtiphy.charts.UnitProfile;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.api.IFeatureFunction;
import org.knowtiphy.shapemap.api.IParsingContext;

/**
 * @author graham
 */
public class ParsingContext implements IParsingContext<MemFeature> {

	private static final Map<String, Function<IFeatureFunction<MemFeature, Object>, IFeatureFunction<MemFeature, Object>>> UNIT_MAP = new HashMap<>();
	static {
		UNIT_MAP.put("knotsToMapUnit", ParsingContext::knotsToMapUnits);
	}

	private final SimpleFeatureType featureType;

	private final UnitProfile unitProfile;

	public ParsingContext(SimpleFeatureType featureType, UnitProfile unitProfile) {
		this.featureType = featureType;
		this.unitProfile = unitProfile;
	}

	private static IFeatureFunction<MemFeature, Object> knotsToMapUnits(IFeatureFunction<MemFeature, Object> quantity) {
		return (f, g) -> {
			var value = quantity.apply(f, g);
			return value == null ? null : ((Number) value).doubleValue() * 1.852;
		};
	}

	@Override
	public IFeatureFunction<MemFeature, Object> compilePropertyAccess(String name) {
		var index1 = featureType.indexOf(name);
		return (f, g) -> f.getAttribute(index1);
	}

	@Override
	public IFeatureFunction<MemFeature, Object> compileFunctionCall(String name,
			Collection<IFeatureFunction<MemFeature, Object>> args) {

		System.err.println("name = " + name);
		var function = UNIT_MAP.get(name);
		return function.apply(args.iterator().next());
	}

}
