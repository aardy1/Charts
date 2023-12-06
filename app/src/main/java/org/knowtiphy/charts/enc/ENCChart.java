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
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.charts.chartview.markicons.ResourceLoader;
import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.api.model.MapViewModel;
import org.knowtiphy.shapemap.renderer.context.RemoveHolesFromPolygon;
import org.knowtiphy.shapemap.renderer.context.RenderGeomCache;
import org.knowtiphy.shapemap.renderer.context.SVGCache;

/**
 * @author graham
 */
public class ENCChart extends MapViewModel<SimpleFeatureType, IFeature> {

	private final ChartDescription chartDescription;

	public ENCChart(ChartLocker chartLocker, ChartDescription chartDescription, CoordinateReferenceSystem crs,
			MapDisplayOptions displayOptions)
			throws TransformException, FactoryException, NonInvertibleTransformException {

		super(chartDescription.getBounds(crs), new RemoveHolesFromPolygon(new RenderGeomCache()),
				new SVGCache(ResourceLoader.class));
		this.chartDescription = chartDescription;
	}

	public ChartDescription getChartDescription() {
		return chartDescription;
	}

	public int cScale() {
		return chartDescription.cScale();
	}

	public double getZoomFactor() {
		return 1 / (viewPortBounds().getWidth() / bounds().getWidth());
	}

	public int currentScale() {
		return (int) (cScale() / getZoomFactor());
	}

	public Collection<Panel> getPanels() {
		return chartDescription.getPanels();
	}

	public String title() {
		return chartDescription.getName();
	}

}
