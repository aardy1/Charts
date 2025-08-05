/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartlocker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.knowtiphy.charts.model.MapModel;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.memstore.StyleReader;
import org.knowtiphy.charts.settings.AppSettings;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;

/** A loader of ENC cells from the local file system */
public class ENCCellLoader {

    private final StyleReader<SimpleFeatureType, MemFeature> styleReader;

    //  loaded cells cache
    private final Map<ENCCell, MapModel<SimpleFeatureType, MemFeature>> loaded = new HashMap<>();

    public ENCCellLoader(StyleReader<SimpleFeatureType, MemFeature> styleReader) {
        this.styleReader = styleReader;
    }

    /** Load a single ENC cell.n */
    public synchronized MapModel<SimpleFeatureType, MemFeature> loadCell(
            ENCCell cell, AppSettings settings, MapDisplayOptions displayOptions)
            throws IOException, XMLStreamException, StyleSyntaxException {

        var map = loaded.get(cell);
        System.err.println("cached " + cell.lname() + " = " + (map != null));
        if (map == null) {
            map = new MapModelReader(cell, settings, styleReader, displayOptions).read();
            loaded.put(cell, map);
        }

        return map;
    }
}