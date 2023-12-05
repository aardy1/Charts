/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.api;

import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * @author graham
 */
public interface IFeatureSource<S, F extends IFeature> {

	IFeatureSourceIterator<S, F> features();

	IFeatureSourceIterator<S, F> features(ReferencedEnvelope bounds, boolean scaleLess);

	S getSchema();

}
