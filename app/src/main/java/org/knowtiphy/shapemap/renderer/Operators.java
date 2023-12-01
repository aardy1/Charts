/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer;

import org.apache.commons.lang3.StringUtils;
import org.geotools.api.feature.simple.SimpleFeature;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.IFeatureFunction;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class Operators {

	public static boolean eq(IFeatureFunction<?> l, IFeatureFunction<?> r, SimpleFeature feature, Geometry geom) {
		return l.apply(feature, geom).equals(r.apply(feature, geom));
	}

	public static boolean ne(IFeatureFunction<?> l, IFeatureFunction<?> r, SimpleFeature feature, Geometry geom) {
		return !eq(l, r, feature, geom);
	}

	public static boolean le(IFeatureFunction<?> l, IFeatureFunction<?> r, SimpleFeature feature, Geometry geom) {
		return ((Comparable) l.apply(feature, geom)).compareTo((Comparable) r.apply(feature, geom)) <= 0;
	}

	public static boolean lt(IFeatureFunction<?> l, IFeatureFunction<?> r, SimpleFeature feature, Geometry geom) {
		return ((Comparable) l.apply(feature, geom)).compareTo((Comparable) r.apply(feature, geom)) < 0;
	}

	public static boolean ge(IFeatureFunction<?> l, IFeatureFunction<?> r, SimpleFeature feature, Geometry geom) {
		return ((Comparable) l.apply(feature, geom)).compareTo((Comparable) r.apply(feature, geom)) >= 0;
	}

	public static boolean gt(IFeatureFunction<?> l, IFeatureFunction<?> r, SimpleFeature feature, Geometry geom) {
		return ((Comparable) l.apply(feature, geom)).compareTo((Comparable) r.apply(feature, geom)) > 0;
	}

	// TODO -- would be good to compile a regexp pattern
	public static boolean like(IFeatureFunction<?> l, IFeatureFunction<?> r, SimpleFeature feature, Geometry geom) {
		return ((String) l.apply(feature, geom)).matches((String) r.apply(feature, geom));
	}

	// this is a bit of a hack
	public static Object coalesce(IFeatureFunction<?> l, IFeatureFunction<?> r, SimpleFeature feature, Geometry geom) {
		var first = l.apply(feature, geom);
		if (first instanceof String firstStr)
			return StringUtils.isBlank(firstStr) ? r.apply(feature, geom) : first;
		else
			return first == null ? r.apply(feature, geom) : first;
	}

}
