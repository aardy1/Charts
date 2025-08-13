/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartlocker;

import java.io.IOException;
import java.util.HashMap;
import javax.xml.stream.XMLStreamException;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.map.Map;
import org.knowtiphy.charts.map.MapReader;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;

/**
 * A loader of ENC cells, loading the cells into Map objects. The loader maintains a cache of loaded
 * cells.
 */
public class ENCCellLoader {

    //  a reader of maps
    private final MapReader mapReader;

    //  loaded cells cache -- probably should limit the size of this
    private final java.util.Map<ENCCell, Map<MemFeature>> loaded = new HashMap<>();

    public ENCCellLoader(MapReader mapReader) {
        this.mapReader = mapReader;
    }

    /**
     * Load a single ENC cell.
     *
     * @param cell the cell
     * @return a map with the map data stored in a mem store.
     * @throws IOException on some sort of IO exception when reading style sheets
     * @throws XMLStreamException if any XML style sheet for the cell is malformed XML
     * @throws StyleSyntaxException if any style sheet for the cell is valid XML but invalid in
     *     other ways
     */
    public synchronized Map<MemFeature> loadCell(ENCCell cell)
            throws IOException, XMLStreamException, StyleSyntaxException {

        var map = loaded.get(cell);
        if (map == null) {
            map = mapReader.read(cell);
            loaded.put(cell, map);
        }

        return map;
    }
}