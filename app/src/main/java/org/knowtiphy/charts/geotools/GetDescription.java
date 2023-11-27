/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.geotools;

import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.geotools.api.feature.Feature;

import static org.knowtiphy.charts.ontology.S57.AT_OBJNAM;

/**
 * @author graham
 */
public class GetDescription {

	public static String description(Feature feature, Collection<String> attributes, String defaultValue) {

		for (var attribute : attributes) {
			var prop = feature.getProperty(attribute);
			if (prop != null && !StringUtils.isBlank((String) prop.getValue()))
				return ((String) prop.getValue()).replace("\n", " ");
		}

		return defaultValue;
	}

	public static String description(Feature feature, String defaultValue) {
		return description(feature, List.of(AT_OBJNAM), defaultValue);
	}

}
