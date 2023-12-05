/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.charts.ontology.S57;
import shapemap.renderer.api.IFeatureSource;
import shapemap.renderer.api.IFeatureSourceIterator;
import org.locationtech.jts.index.strtree.STRtree;

/**
 * @author graham
 */
public class MemStoreFeatureSource // extends ContentFeatureSource
		implements IFeatureSource<SimpleFeatureType, MemFeature> {

	private final ENCChart map;

	private final SimpleFeatureType featureType;

	private final STRtree featureIndex;

	// TODO -- we really shouldn't be passing the map -- should be in the query
	public MemStoreFeatureSource(ENCChart map, SimpleFeatureType featureType, STRtree featureIndex) {

		// super(entry, query);

		this.map = map;
		this.featureType = featureType;
		this.featureIndex = featureIndex;
	}

	// @Override
	// public FeatureReader<SimpleFeatureType, SimpleFeature> getReaderInternal(Query
	// query) throws IOException {
	//
	// assert false;
	// // System.err.println("Query = " + (query instanceof MemStoreQuery));
	// // System.err.println("Query = " + query);
	//
	// Collection<SimpleFeature> featuresInScale;
	// if (query instanceof MemStoreQuery mq) {
	//
	// var ebounds = System.currentTimeMillis();
	// var featuresInBounds = (List<SimpleFeature>) featureIndex.query(mq.bounds());
	// ebounds = System.currentTimeMillis() - ebounds;
	//
	// var sbounds = System.currentTimeMillis();
	//
	// var currentScale = map.currentScale();
	// var scaleLess = mq.scaleLess();
	//
	// featuresInScale = new ArrayList<>();
	// for (var feature : featuresInBounds) {
	// var featureMinScale = feature.getAttribute(S57.AT_SCAMIN);
	// if (scaleLess || featureMinScale == null || (int) featureMinScale >= currentScale)
	// {
	// featuresInScale.add(feature);
	// }
	// }
	//
	// sbounds = System.currentTimeMillis() - sbounds;
	//
	// // System.err.println("Source " + featureType.getTypeName() + ", #In Bounds =
	// // " + featuresInBounds.size()
	// // + " : " + ebounds + " millis, #In Scale = " + featuresInScale.size() + " :
	// // " + sbounds
	// // + " millis, current scale = " + currentScale + ", scaleLess = " +
	// // scaleLess);
	// }
	// else {
	// var ebounds = System.currentTimeMillis();
	// featuresInScale = featureIndex.query(map.viewPortBounds());
	// ebounds = System.currentTimeMillis() - ebounds;
	//
	// // System.err.println("Source " + featureType.getTypeName() + ", #In Bounds =
	// // " + featuresInScale.size()
	// // + " : " + ebounds + " millis");
	// }
	//
	// return null;
	// // return new MemStoreFeatureReader(getState(), featureType,
	// // featuresInScale.iterator());
	// }

	public SimpleFeatureType getSchema() {

		return featureType;
	}

	public IFeatureSourceIterator<SimpleFeatureType, MemFeature> features(ReferencedEnvelope bounds,
			boolean scaleLess) {
		Collection<MemFeature> featuresInScale;

		var ebounds = System.currentTimeMillis();
		var featuresInBounds = (List<MemFeature>) featureIndex.query(bounds);
		ebounds = System.currentTimeMillis() - ebounds;

		var sbounds = System.currentTimeMillis();

		var currentScale = map.currentScale();

		featuresInScale = new ArrayList<>();
		for (var feature : featuresInBounds) {
			var featureMinScale = feature.getAttribute(S57.AT_SCAMIN);
			if (scaleLess || featureMinScale == null || (int) featureMinScale >= currentScale) {
				featuresInScale.add(feature);
			}
		}

		sbounds = System.currentTimeMillis() - sbounds;

		// System.err.println("Source " + featureType.getTypeName() + ", #In Bounds =
		// " + featuresInBounds.size()
		// + " : " + ebounds + " millis, #In Scale = " + featuresInScale.size() + " :
		// " + sbounds
		// + " millis, current scale = " + currentScale + ", scaleLess = " +
		// scaleLess);

		return new MemStoreFeatureReader(featureType, featuresInScale.iterator());
	}

	public IFeatureSourceIterator<SimpleFeatureType, MemFeature> features() {

		Collection<MemFeature> featuresInScale;

		var ebounds = System.currentTimeMillis();
		var featuresInBounds = (List<MemFeature>) featureIndex.itemsTree();
		ebounds = System.currentTimeMillis() - ebounds;

		var sbounds = System.currentTimeMillis();

		var currentScale = map.currentScale();

		featuresInScale = new ArrayList<>();
		for (var feature : featuresInBounds) {
			var featureMinScale = feature.getAttribute(S57.AT_SCAMIN);
			if (featureMinScale == null || (int) featureMinScale >= currentScale) {
				featuresInScale.add(feature);
			}
		}

		sbounds = System.currentTimeMillis() - sbounds;

		// System.err.println("Source " + featureType.getTypeName() + ", #In Bounds =
		// " + featuresInBounds.size()
		// + " : " + ebounds + " millis, #In Scale = " + featuresInScale.size() + " :
		// " + sbounds
		// + " millis, current scale = " + currentScale + ", scaleLess = " +
		// scaleLess);

		return new MemStoreFeatureReader(featureType, featuresInScale.iterator());
	}

	// @Override
	// protected int getCountInternal(Query query) {
	// // can do better here
	// return featureIndex.size();
	// }
	//
	// @Override
	// protected ReferencedEnvelope getBoundsInternal(Query query) throws IOException {
	// return !(query instanceof MemStoreQuery) ? map.viewPortBounds() : ((MemStoreQuery)
	// query).bounds();
	// }
	//
	// @Override
	// protected synchronized SimpleFeatureType buildFeatureType() throws IOException {
	// return featureType;
	// }
	//
	// @Override
	// protected boolean canFilter() {
	// return true;
	// }

}