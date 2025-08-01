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
import org.locationtech.jts.geom.Coordinate;

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

    private static final String PANEL_NO = "panel_no";

    private static final String VERTEX = "vertex";

    private static final String LAT = "lat";

    private static final String LONG = "long";

    private final Path chartsDir;

    private ENCProductCatalog catalog;

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
    public ENCProductCatalog read() throws XMLStreamException {
        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(stream);

        ENCCell cell = null;
        ENCPanel panel = null;
        Coordinate coordinate = null;

        catalog = new ENCProductCatalog();

        while (reader.hasNext()) {
            var nextEvent = reader.nextEvent();

            if (nextEvent.isStartElement()) {
                var startElement = nextEvent.asStartElement();
                switch (startElement.getName().getLocalPart()) {
                    case TITLE -> {
                        catalog.setTitle(reader.nextEvent().asCharacters().getData());
                    }
                    case CELL -> {
                        assert catalog != null;
                        cell = new ENCCell(Naming.regionName(catalog));
                    }
                    case CELL_NAME -> {
                        assert cell != null;
                        cell.setName(reader.nextEvent().asCharacters().getData());
                        cell.setLocation(Naming.cellName(chartsDir, cell));
                    }
                    case CELL_LNAME -> {
                        assert cell != null;
                        cell.setLname(reader.nextEvent().asCharacters().getData());
                    }
                    case CSCALE -> {
                        assert cell != null;
                        cell.setcScale(
                                Integer.parseInt(reader.nextEvent().asCharacters().getData()));
                    }
                    case STATUS -> {
                        assert cell != null;
                        cell.setActive(
                                reader.nextEvent()
                                        .asCharacters()
                                        .getData()
                                        .equalsIgnoreCase("active"));
                    }
                    case ZIP_FILE_LOCATION -> {
                        assert cell != null;
                        cell.setZipFileLocation(reader.nextEvent().asCharacters().getData());
                    }
                    case PANEL -> panel = new ENCPanel();
                    //                    case PANEL_NO ->
                    //                    {
                    //                        assert panel != null;
                    //                        panel.setPanelNumber(
                    //
                    // Integer.parseInt(reader.nextEvent().asCharacters().getData()));
                    //                    }
                    case VERTEX -> coordinate = new Coordinate();
                    case LONG -> {
                        assert coordinate != null;
                        coordinate.x =
                                Double.parseDouble(reader.nextEvent().asCharacters().getData());
                    }
                    case LAT -> {
                        assert coordinate != null;
                        coordinate.y =
                                Double.parseDouble(reader.nextEvent().asCharacters().getData());
                    }
                    default -> {
                        // ignore other tags
                    }
                }
            }

            if (nextEvent.isEndElement()) {
                var endElement = nextEvent.asEndElement();
                switch (endElement.getName().getLocalPart()) {
                    case CELL -> catalog.addCell(cell);
                    case PANEL -> {
                        assert panel != null;
                        assert cell != null;
                        cell.addPanel(panel);
                    }
                    case VERTEX -> {
                        assert panel != null;
                        panel.addVertex(coordinate);
                    }
                    default -> {
                        // ignore other tags
                    }
                }
            }
        }

        return catalog;
    }
}
