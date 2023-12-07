/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import org.geotools.api.feature.simple.SimpleFeatureType;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.api.ISchemaAdapter;

/**
 * @author graham
 */
public class SchemaAdapter implements ISchemaAdapter<SimpleFeatureType, MemFeature> {

	public static final SchemaAdapter ADAPTER = new SchemaAdapter();

	private SchemaAdapter() {
	}

	@Override
	public String name(SimpleFeatureType type) {
		return type.getTypeName();
	}

}
