/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chart;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.memstore.StyleReader;
import org.knowtiphy.charts.settings.AppSettings;
import org.knowtiphy.shapemap.model.MapModel;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;

/** A loader of maps from a local ENC chart cache. */
public class ChartLoader {
    private final AppSettings settings;

    private final StyleReader<SimpleFeatureType, MemFeature> styleReader;

    private final Map<ENCCell, MapModel<SimpleFeatureType, MemFeature>> loaded = new HashMap<>();

    public ChartLoader(
            AppSettings settings, StyleReader<SimpleFeatureType, MemFeature> styleReader) {
        this.styleReader = styleReader;
        this.settings = settings;
    }

    MapModel<SimpleFeatureType, MemFeature> loadMap(ENCCell cell, MapDisplayOptions displayOptions)
            throws IOException, XMLStreamException, StyleSyntaxException {
        var map = loaded.get(cell);
        System.err.println("cached " + cell.lname() + " = " + (map != null));
        if (map == null) {
            map = new ChartBuilder(cell, settings, styleReader, displayOptions).build();
            loaded.put(cell, map);
        }

        return map;
    }
}