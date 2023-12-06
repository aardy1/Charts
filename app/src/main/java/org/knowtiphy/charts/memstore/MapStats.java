/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

/**
 * @author graham
 */
public class MapStats<S, F> {

	// private final MapViewModel<S, F> map;
	//
	// private final Map<String, Integer> counts = new HashMap<>();
	//
	// private final Map<String, Integer> nullMinScale = new HashMap<>();
	//
	// private final Map<String, Integer> nullMaxScale = new HashMap<>();
	//
	// private final Map<String, Integer> minScale = new HashMap<>();
	//
	// private final Map<String, Integer> maxScale = new HashMap<>();
	//
	// private final Map<String, Integer> mp = new HashMap<>();
	//
	// public MapStats(MapViewModel<S, F> map) {
	// this.map = map;
	// }
	//
	// public MapStats stats() throws IOException {
	//
	// for (var layer : map.layers()) {
	// var layerSize = layer.getFeatureSource().getFeatures().size();
	// var type = layer.getFeatureSource().getSchema().getName();
	// counts.put(type, layerSize);
	// updateScaleInfo(layer, type);
	// }
	//
	// return this;
	// }
	//
	// public void print() throws IOException, TransformException, FactoryException {
	//
	// var sum = 0;
	// for (var layer : map.layers()) {
	// var layerSize = layer.getFeatureSource().getFeatures().size();
	// sum += layerSize;
	// }
	//
	// var keys = new ArrayList<>(counts.keySet());
	// keys.sort((a, b) -> a.compareTo(b));// (a, b) -> counts.get(b) - counts.get(a));
	//
	// System.err.println();
	// System.err.printf("%-6s %-7s %-12s %-12s %-10s %-10s %-10s%n", "type", "#", "#null
	// SCAMIN", "#null SCAMAX",
	// AT_SCAMIN, AT_SCAMAX, "#MP");
	// for (var key : keys)
	// System.err.printf("%-6s %-7d %-12s %-12s %-10s %-10s %-10s%n", key,
	// counts.get(key),
	// N(nullMinScale.get(key)), N(nullMaxScale.get(key)), N(minScale.get(key)),
	// N(maxScale.get(key)),
	// N(mp.get(key)));
	//
	// System.err.println();
	// System.err.println("Total num features = " + sum);
	// System.err.println();
	//
	// var mapSpans = distanceAcross(map) / 1000;
	// System.err.println("Map span = " + mapSpans + " km");
	// System.err.println("Map span = " + ENC.kmToNM(mapSpans) + " nm");
	// System.err.println();
	//
	// System.err.println("Map title " + map.title());
	//
	// System.err.println();
	// }
	//
	// public Map<String, Integer> getMinScale() {
	// return minScale;
	// }
	//
	// public Map<String, Integer> getMaxScale() {
	// return maxScale;
	// }
	//
	// private String N(Integer value) {
	// return value == null ? "N/A" : (value + "");
	// }
	//
	// private void updateScaleInfo(MapLayer layer, String type) throws Exception {
	// try (var features = layer.getFeatureSource().getFeatures()) {
	// {
	// while (features.hasNext()) {
	// var feature = features.next();
	// updateNilCount(type, feature, AT_SCAMIN, nullMinScale);
	// updateNilCount(type, feature, AT_SCAMAX, nullMaxScale);
	// updateMinMaxes(type, feature, AT_SCAMIN, minScale, Integer.MAX_VALUE, Math::min);
	// updateMinMaxes(type, feature, AT_SCAMAX, maxScale, Integer.MIN_VALUE, Math::max);
	// updateMP(type, feature);
	// }
	// }
	// }
	// }
	//
	// private void ensureInitialized(Map<String, Integer> map, String property, int
	// initialValue) {
	// map.computeIfAbsent(property, k -> initialValue);
	// }
	//
	// private void updateNilCount(String type, IFeature feature, String property,
	// Map<String, Integer> count) {
	// var prop = feature.getProperty(property);
	// ensureInitialized(count, type, 0);
	// if (prop.getValue() == null) {
	// count.put(type, count.get(type) + 1);
	// }
	// }
	//
	// private void updateMinMaxes(String type, IFeature feature, String property,
	// Map<String, Integer> minMaxVals,
	// int initialValue, IntBinaryOperator nextVal) {
	// var prop = feature.getProperty(property);
	// if (prop.getValue() != null) {
	// ensureInitialized(minMaxVals, type, initialValue);
	// minMaxVals.put(type, nextVal.applyAsInt(minMaxVals.get(type), (int)
	// prop.getValue()));
	// }
	// }
	//
	// private void updateMP(String type, IFeature feature) {
	// var prop = feature.getDefaultGeometryProperty().getType();
	// // System.err.println("GP = " + prop);
	// // assert !prop.getName().getLocalPart().equals("MultiPoint");
	// ensureInitialized(mp, type, 0);
	// if (prop.getName().getLocalPart().equals("MultiPoint")) {
	// mp.put(type, mp.get(type) + 1);
	// }
	// }

}