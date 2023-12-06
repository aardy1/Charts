/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer;

import org.apache.commons.lang3.StringUtils;
import org.knowtiphy.shapemap.api.IFeatureFunction;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class Operators {

	public static <F> boolean eq(IFeatureFunction<F, ?> l, IFeatureFunction<F, ?> r, F feature, Geometry geom) {
		return l.apply(feature, geom).equals(r.apply(feature, geom));
	}

	public static <F> boolean ne(IFeatureFunction<F, ?> l, IFeatureFunction<F, ?> r, F feature, Geometry geom) {
		return !eq(l, r, feature, geom);
	}

	public static <F> boolean le(IFeatureFunction<F, ?> l, IFeatureFunction<F, ?> r, F feature, Geometry geom) {
		return ((Comparable) l.apply(feature, geom)).compareTo((Comparable) r.apply(feature, geom)) <= 0;
	}

	public static <F> boolean lt(IFeatureFunction<F, ?> l, IFeatureFunction<F, ?> r, F feature, Geometry geom) {
		return ((Comparable) l.apply(feature, geom)).compareTo((Comparable) r.apply(feature, geom)) < 0;
	}

	public static <F> boolean ge(IFeatureFunction<F, ?> l, IFeatureFunction<F, ?> r, F feature, Geometry geom) {
		return ((Comparable) l.apply(feature, geom)).compareTo((Comparable) r.apply(feature, geom)) >= 0;
	}

	public static <F> boolean gt(IFeatureFunction<F, ?> l, IFeatureFunction<F, ?> r, F feature, Geometry geom) {
		return ((Comparable) l.apply(feature, geom)).compareTo((Comparable) r.apply(feature, geom)) > 0;
	}

	// TODO -- would be good to compile a regexp pattern
	public static <F> boolean like(IFeatureFunction<F, ?> l, IFeatureFunction<F, ?> r, F feature, Geometry geom) {
		return ((String) l.apply(feature, geom)).matches((String) r.apply(feature, geom));
	}

	// this is a bit of a hack
	public static <F> Object coalesce(IFeatureFunction<F, ?> l, IFeatureFunction<F, ?> r, F feature, Geometry geom) {
		var first = l.apply(feature, geom);
		if (first instanceof String firstStr)
			return StringUtils.isBlank(firstStr) ? r.apply(feature, geom) : first;
		else
			return first == null ? r.apply(feature, geom) : first;
	}

	public static <F> String toString(IFeatureFunction<F, ?> f, F feature, Geometry geom) {
		var result = f.apply(feature, geom);
		return result == null ? null : result.toString();
	}

}
