/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import java.util.Collection;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.chartview.markicons.ResourceLoader;
import org.knowtiphy.shapemap.renderer.api.IFeature;
import org.knowtiphy.shapemap.renderer.context.SVGCache;
import org.knowtiphy.shapemap.viewmodel.MapDisplayOptions;
import org.knowtiphy.shapemap.viewmodel.MapViewModel;

/**
 * @author graham
 */
public class ENCChart extends MapViewModel<SimpleFeatureType, IFeature> {

	private final ChartDescription chartDescription;

	public ENCChart(ChartLocker chartLocker, ChartDescription chartDescription, CoordinateReferenceSystem crs,
			MapDisplayOptions displayOptions)
			throws TransformException, FactoryException, NonInvertibleTransformException {

		super(chartDescription.getName(), chartDescription.getBounds(crs), new SVGCache(ResourceLoader.class));
		this.chartDescription = chartDescription;
	}

	public ChartDescription getChartDescription() {
		return chartDescription;
	}

	public int cScale() {
		return chartDescription.cScale();
	}

	public int currentScale() {
		return (int) (cScale() / getZoomFactor());
	}

	public Collection<Panel> getPanels() {
		return chartDescription.getPanels();
	}

}
