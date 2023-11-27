/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import org.geotools.api.data.Query;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * @author graham
 */
public class MemStoreQuery extends Query {

	private final ReferencedEnvelope bounds;

	private final boolean scaleLess;

	public MemStoreQuery(ReferencedEnvelope bounds, boolean scaleLess) {
		this.bounds = bounds;
		this.scaleLess = scaleLess;
	}

	public ReferencedEnvelope bounds() {
		return bounds;
	}

	public boolean scaleLess() {
		return scaleLess;
	}

	@Override
	public String toString() {
		return "MyQuery{" + "envelope=" + bounds + ',' + super.toString() + '}';
	}

}
