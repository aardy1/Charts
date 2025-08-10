/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.enc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.knowtiphy.charts.enc.builder.ENCCellBuilder;
import org.knowtiphy.charts.enc.builder.ENCPanelBuilder;
import org.knowtiphy.charts.enc.builder.ENCCatalogBuilder;
import org.knowtiphy.charts.enc.builder.VertexBuilder;

/** A reader of ENC Catalogs (in XML format) from a path. */
public class ENCCatalogReader {

    private static final String TITLE = "title";

    private static final String CELL = "cell";

    private static final String CELL_NAME = "name";

    private static final String CELL_LNAME = "lname";

    private static final String CSCALE = "cscale";

    private static final String STATUS = "status";

    private static final String ZIP_FILE_LOCATION = "zipfile_location";

    private static final String PANEL = "panel";

    //    private static final String PANEL_NO = "panel_no";

    private static final String VERTEX = "vertex";

    private static final String LAT = "lat";

    private static final String LONG = "long";

    private final Path chartsDir;

    private final InputStream stream;

    public ENCCatalogReader(Path chartsDir, Path catalogFile) throws FileNotFoundException {
        this.chartsDir = chartsDir;
        stream = new FileInputStream(catalogFile.toFile());
    }

    /**
     * Read a catalog from a stream.
     *
     * @return the catalog
     * @throws XMLStreamException on a malformed catalog file
     */
    @SuppressWarnings("null")
    public ENCCatalog read() throws XMLStreamException {
        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(stream);

        ENCCatalogBuilder catalogBuilder = new ENCCatalogBuilder(chartsDir);
        ENCCellBuilder cellBuilder = null;
        ENCPanelBuilder panelBuilder = null;
        VertexBuilder vertexBuilder = null;

        while (reader.hasNext()) {
            var nextEvent = reader.nextEvent();

            if (nextEvent.isStartElement()) {
                var startElement = nextEvent.asStartElement();
                switch (startElement.getName().getLocalPart()) {
                    case TITLE -> {
                        assert catalogBuilder != null;
                        catalogBuilder.setTitle(reader.nextEvent().asCharacters().getData());
                    }
                    case CELL -> {
                        cellBuilder = new ENCCellBuilder();
                    }
                    case CELL_NAME -> {
                        assert cellBuilder != null;
                        var cellName = reader.nextEvent().asCharacters().getData();
                        cellBuilder.setName(cellName);
                        cellBuilder.location(catalogBuilder.cellPath(cellName));
                    }
                    case CELL_LNAME -> {
                        assert cellBuilder != null;
                        cellBuilder.setTitle(reader.nextEvent().asCharacters().getData());
                    }
                    case CSCALE -> {
                        assert cellBuilder != null;
                        cellBuilder.cScale(
                                Integer.parseInt(reader.nextEvent().asCharacters().getData()));
                    }
                    case STATUS -> {
                        assert cellBuilder != null;
                        cellBuilder.setActive(
                                reader.nextEvent()
                                        .asCharacters()
                                        .getData()
                                        .equalsIgnoreCase("active"));
                    }
                    case ZIP_FILE_LOCATION -> {
                        assert cellBuilder != null;
                        cellBuilder.zipFileLocation(reader.nextEvent().asCharacters().getData());
                    }
                    case PANEL -> panelBuilder = new ENCPanelBuilder();
                    case VERTEX -> vertexBuilder = new VertexBuilder();
                    case LONG -> {
                        assert vertexBuilder != null;
                        vertexBuilder.setLongitude(
                                Double.parseDouble(reader.nextEvent().asCharacters().getData()));
                    }
                    case LAT -> {
                        assert vertexBuilder != null;
                        vertexBuilder.setLatitude(
                                Double.parseDouble(reader.nextEvent().asCharacters().getData()));
                    }
                    default -> {
                        // ignore other tags
                    }
                }
            }

            if (nextEvent.isEndElement()) {
                var endElement = nextEvent.asEndElement();
                switch (endElement.getName().getLocalPart()) {
                    case CELL -> {
                        assert catalogBuilder != null;
                        assert cellBuilder != null;
                        var cell = cellBuilder.build();
                        //  cell null means the cell was marked as inactive so ignore it
                        if (cell != null) catalogBuilder.addCell(cell);
                    }
                    case PANEL -> {
                        assert panelBuilder != null;
                        assert cellBuilder != null;
                        cellBuilder.panel(panelBuilder.build());
                    }
                    case VERTEX -> {
                        assert panelBuilder != null;
                        assert vertexBuilder != null;
                        panelBuilder.addVertex(vertexBuilder.build());
                    }
                    default -> {
                        // ignore other tags
                    }
                }
            }
        }

        return catalogBuilder.build();
    }
}
