/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import java.util.Collection;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.shapemap.viewmodel.MapDisplayOptions;
import org.knowtiphy.shapemap.viewmodel.MapViewModel;
import org.reactfx.EventSource;

/**
 * @author graham
 */
public class ENCChart extends MapViewModel {

	public final EventSource<ENCChart> newChartEvents = new EventSource<>();

	private final ChartDescription chartDescription;

	public ENCChart(ChartLocker chartLocker, ChartDescription chartDescription, CoordinateReferenceSystem crs,
			MapDisplayOptions displayOptions)
			throws TransformException, FactoryException, NonInvertibleTransformException {

		super(chartDescription.getName(), chartDescription.getBounds(crs), displayOptions);
		this.chartDescription = chartDescription;
		// TODO -- need to unsubscibe?
		newChartEvents.feedFrom(chartLocker.chartLoadedEvents);
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
