/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import org.knowtiphy.shapemap.renderer.InternalMapViewModel;

/**
 * @author graham
 */
public class ShapeMapViewFactory {

	public static <S, F extends IFeature> ShapeMapView create(IMapViewModel<S, F> mapViewModel) {
		return new ShapeMapView(new InternalMapViewModel<>(mapViewModel));
	}

}
