/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.transform.NonInvertibleTransformException;
import javax.xml.stream.XMLStreamException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.memstore.StyleReader;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.viewmodel.MapDisplayOptions;

/**
 * @author graham
 */
public class LocalChartProvider {

	private final StyleReader styleReader;

	private final List<ChartDescription> chartDescriptions = new ArrayList<>();

	public LocalChartProvider(Catalog catalog, Path shapeBaseDir, StyleReader styleReader) {

		this.styleReader = styleReader;

		for (var cell : catalog.getCells()) {
			var dir = shapeBaseDir
					.resolve(cell.getName().replaceAll(" ", "_").replaceAll(",", "_") + "_" + cell.getcScale());
			chartDescriptions.add(new ChartDescription(dir, cell));
		}
	}

	// TODO -- needs to go away or be smarter
	public ChartDescription getChartDescription(String lname, int cscale) {
		for (var chartDescription : chartDescriptions) {
			if (chartDescription.getCell().getLname().equals(lname) && chartDescription.cScale() == cscale)
				return chartDescription;
		}

		return null;
	}

	public List<ChartDescription> getChartDescriptions() {
		return chartDescriptions;
	}

	public ENCChart loadChart(ChartLocker chartLocker, ChartDescription chartDescription,
			MapDisplayOptions displayOptions) throws IOException, XMLStreamException, TransformException,
			FactoryException, NonInvertibleTransformException, StyleSyntaxException {

		System.err.println("Desc = " + chartDescription);
		var reader = new ChartBuilder(chartLocker, chartDescription.getDir(), chartDescription, styleReader,
				displayOptions).read();
		var map = reader.getMap();
		map.setViewPortBounds(map.bounds());
		return map;
		// return new ENCChart(chartDescription, map.crs(), displayOptions);
	}

	public ENCChart loadChart(ChartLocker chartLocker, ChartDescription chartDescription, ReferencedEnvelope bounds,
			MapDisplayOptions displayOptions) throws IOException, XMLStreamException, TransformException,
			FactoryException, NonInvertibleTransformException, StyleSyntaxException {

		var reader = new ChartBuilder(chartLocker, chartDescription.getDir(), chartDescription, styleReader,
				displayOptions).read();
		var map = reader.getMap();
		map.setViewPortBounds(bounds);
		return map;
		// return new ENCChart(chartDescription, map.crs(), displayOptions);
	}

}
