/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.Name;
import org.geotools.api.feature.type.PropertyDescriptor;
import org.geotools.api.filter.identity.FeatureId;
import org.knowtiphy.shapemap.api.model.MapLayer;
import org.knowtiphy.shapemap.api.model.MapViewModel;

/**
 * @author graham
 */
public class Dump<S extends SimpleFeatureType, F> {

	private final MapViewModel<S, F> mapContent;

	private final Set<PropertyDescriptor> descriptors = new HashSet<>();

	private final Map<FeatureId, Map<Name, List<Object>>> featureProperties = new HashMap<>();

	public Dump(MapViewModel<S, F> mapContent) {
		this.mapContent = mapContent;
	}

	public void dump() throws IOException {
		dump(null);
	}

	public void dump(String featureType) throws IOException {
		var j = 0;
		for (var layer : mapContent.layers()) {
			System.err.println("layer = " + layer.getFeatureSource().getSchema().getName().getLocalPart());
			if (featureType == null
					|| layer.getFeatureSource().getSchema().getName().getLocalPart().equals(featureType)) {
				dumpSchema(layer);
				dumpAttributeValues(layer);
				j++;
			}
		}
	}

	public void dumpSchema(MapLayer<S, F> layer) throws IOException {

		var schema = layer.getFeatureSource().getSchema();
		descriptors.addAll(schema.getDescriptors());
	}

	public void dumpAttributeValues(MapLayer<S, F> layer) throws IOException {

		// var featureName =
		// layer.getFeatureSource().getSchema().getName().getLocalPart();
		// var query = new MemStoreQuery(mapContent.getMaxBounds(), new
		// Scale(mapContent));
		// try (var features = layer.getFeatureSource().getFeatures(query).features()) {
		// int i = 0;
		// while (features.hasNext()) {
		//
		// var feature = features.next();
		//
		// if (!featureProperties.containsKey(feature.getIdentifier())) {
		// featureProperties.put(feature.getIdentifier(), new HashMap<>());
		// }
		//
		// var featureProperty = featureProperties.get(feature.getIdentifier());
		//
		// System.err.println(featureName + ":" + i);
		// for (var property : feature.getProperties()) {
		// if (!featureProperty.containsKey(property.getName())) {
		// featureProperty.put(property.getName(), new ArrayList<>());
		// }
		// System.err.println(property.getName() + " = " + property.getValue());
		//
		// featureProperty.get(property.getName()).add(property.getValue());
		// }
		//
		// i++;
		// }
		// }
	}

	public Set<PropertyDescriptor> getDescriptors() {
		return descriptors;
	}

	public void printDescriptors() {
		descriptors.forEach(d -> System.err.println(d));
	}

	public void dumpFeatureTypes() throws Exception {
		var j = 0;
		for (var layer : mapContent.layers()) {
			System.err.println("Layer = " + j);
			try (var features = layer.getFeatureSource().features()) {
				while (features.hasNext()) {
					var feature = features.next();
					// System.err.println("\tFeature Type = " + feature.getType());
				}

			}
			j++;
		}

	}

	public void printDescriptorValues() {
		for (var entry : featureProperties.entrySet()) {
			System.err.println(entry.getKey());
			for (var pentry : entry.getValue().entrySet()) {
				System.err.println(pentry.getKey() + " -> " + entry.getValue());

			}
		}
	}

}