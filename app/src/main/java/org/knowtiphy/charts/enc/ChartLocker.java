/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.NonInvertibleTransformException;
import javax.xml.stream.XMLStreamException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;

/**
 * @author graham
 */
public class ChartLocker {

	// eventually this will be a list of providers
	private final LocalChartProvider chartProvider;

	public ChartLocker(LocalChartProvider chartProvider) {
		this.chartProvider = chartProvider;
	}

	public Collection<ChartDescription> intersections(ReferencedEnvelope envelope) {

		var bounds = JTS.toGeometry(envelope);

		var result = new ArrayList<ChartDescription>();
		for (var chartDescription : chartProvider.getChartDescriptions()) {
			if (chartDescription.intersects(bounds))
				result.add(chartDescription);
		}

		return result;
	}

	public ENCChart getChart(ChartDescription chartDescription, MapDisplayOptions displayOptions)
			throws IOException, XMLStreamException, TransformException, FactoryException,
			NonInvertibleTransformException, StyleSyntaxException {

		return chartProvider.loadChart(this, chartDescription, displayOptions);
	}

	public ENCChart loadChart(ChartDescription chartDescription, MapDisplayOptions displayOptions,
			Rectangle2D screenArea)
			throws TransformException, FactoryException, NonInvertibleTransformException, StyleSyntaxException {

		ENCChart newChart;
		try {
			newChart = getChart(chartDescription, displayOptions);
		}
		catch (IOException | XMLStreamException ex) {
			Logger.getLogger(ChartLocker.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}

		// newChart.setViewPortScreenArea(screenArea);
		newChart.setViewPortBounds(newChart.bounds());
		return newChart;
	}

}
