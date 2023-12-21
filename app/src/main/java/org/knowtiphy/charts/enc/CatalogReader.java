/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import org.locationtech.jts.geom.Coordinate;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

/**
 * @author graham
 */
public class CatalogReader
{
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

  private Catalog catalog;

  private final InputStream stream;

  public CatalogReader(Path chartsDir, Path catalogFile) throws FileNotFoundException
  {
    this.chartsDir = chartsDir;
    stream = new FileInputStream(catalogFile.toFile());
  }

  public CatalogReader(Path chartsDir, URL catalogFile) throws IOException
  {
    this.chartsDir = chartsDir;
    stream = catalogFile.openStream();
  }

  /**
   * Read a catalog from a stream.
   *
   * @return the catalog
   * @throws XMLStreamException on a malformed catalog file
   */
  @SuppressWarnings("null")

  public Catalog read() throws XMLStreamException
  {
    XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(stream);

    ENCCell cell = null;
    Panel panel = null;
    Coordinate coordinate = null;

    while(reader.hasNext())
    {
      var nextEvent = reader.nextEvent();

      if(nextEvent.isStartElement())
      {
        var startElement = nextEvent.asStartElement();
        switch(startElement.getName().getLocalPart())
        {
          case TITLE ->
          {
            catalog = new Catalog();
            catalog.setTitle(reader.nextEvent().asCharacters().getData());
          }
          case CELL ->
          {
            assert catalog != null;
            cell = new ENCCell(catalog);
          }
          case CELL_NAME ->
          {
            assert cell != null;
            cell.setName(reader.nextEvent().asCharacters().getData());
            cell.setLocation(Naming.cellName(chartsDir, cell));
          }
          case CELL_LNAME ->
          {
            assert cell != null;
            cell.setLname(reader.nextEvent().asCharacters().getData());
          }
          case CSCALE ->
          {
            assert cell != null;
            cell.setcScale(Integer.parseInt(reader.nextEvent().asCharacters().getData()));
          }
          case STATUS ->
          {
            assert cell != null;
            cell.setActive(reader.nextEvent().asCharacters().getData().equalsIgnoreCase("active"));
          }
          case ZIP_FILE_LOCATION ->
          {
            assert cell != null;
            cell.setZipFileLocation(reader.nextEvent().asCharacters().getData());
          }
          case PANEL -> panel = new Panel();
          case PANEL_NO ->
          {
            assert panel != null;
            panel.setPanelNumber(Integer.parseInt(reader.nextEvent().asCharacters().getData()));
          }
          case VERTEX -> coordinate = new Coordinate();
          case LONG ->
          {
            assert coordinate != null;
            coordinate.x = Double.parseDouble(reader.nextEvent().asCharacters().getData());
          }
          case LAT ->
          {
            assert coordinate != null;
            coordinate.y = Double.parseDouble(reader.nextEvent().asCharacters().getData());
          }
          default ->
          {
            // do nothing
          }
        }
      }

      if(nextEvent.isEndElement())
      {
        var endElement = nextEvent.asEndElement();
        switch(endElement.getName().getLocalPart())
        {
          case CELL -> catalog.addCell(cell);
          case PANEL ->
          {
            assert panel != null;
            panel.createGeom();
            assert cell != null;
            cell.addPanel(panel);
          }
          case VERTEX ->
          {
            assert panel != null;
            panel.addVertex(coordinate);
          }
        }
      }
    }

    return catalog;
  }
}