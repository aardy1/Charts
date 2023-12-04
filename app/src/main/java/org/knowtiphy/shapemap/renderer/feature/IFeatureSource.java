/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.feature;

import java.io.IOException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * @author graham
 */
public interface IFeatureSource<T, F extends IFeature> {

	IFeatureSourceIterator<T, F> getFeatures(ReferencedEnvelope bounds, boolean scaleLess);

	IFeatureSourceIterator<T, F> features();

	SimpleFeatureType getSchema();

	ReferencedEnvelope getBounds() throws IOException;

}
