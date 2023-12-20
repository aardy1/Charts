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

  private static final String ZIP_FILE_LOCATION = "zipfile_location";

  private static final String PANEL = "panel";

  private static final String PANEL_NO = "panel_no";

  private static final String VERTEX = "vertex";

  private static final String LAT = "lat";

  private static final String LONG = "long";

  private Catalog catalog;

  private InputStream stream;

  public CatalogReader(Path catalogFile) throws FileNotFoundException
  {
    stream = new FileInputStream(catalogFile.toFile());
  }

  public CatalogReader(URL catalogFile) throws IOException
  {
    stream = catalogFile.openStream();
  }

  /**
   * Read an exchange set from a catalog file, constructing a map from scale -> list of
   * cells at that scale.
   *
   * @return
   * @throws FileNotFoundException
   * @throws XMLStreamException
   */
  @SuppressWarnings("null")
  public Catalog read() throws XMLStreamException, IOException
  {

    var xmlInputFactory = XMLInputFactory.newInstance();
    XMLEventReader reader;
    // try
    // {
    reader = xmlInputFactory.createXMLEventReader(stream);
    // }
    // catch(Exception ex)
    // {
    // System.err.println("catFile " + catalogFile);
    // var fileClient = FileClient.create(catalogFile.toFile());
    // System.err.println("File = " + fileClient);
    // System.err.println("DS = " + fileClient.createFileDataSource());
    // System.err.println("Stream = " +
    // fileClient.createFileDataSource().getInputStream());
    // reader = xmlInputFactory.createXMLEventReader(fileClient.createFileDataSource()
    // .getInputStream());
    // }
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
            nextEvent = reader.nextEvent();
            catalog.setTitle(nextEvent.asCharacters().getData());
          }
          case CELL ->
          {
            cell = new ENCCell();
          }
          case CELL_NAME ->
          {
            nextEvent = reader.nextEvent();
            cell.setName(nextEvent.asCharacters().getData());
          }
          case CELL_LNAME ->
          {
            nextEvent = reader.nextEvent();
            cell.setLname(nextEvent.asCharacters().getData());
          }
          case CSCALE ->
          {
            nextEvent = reader.nextEvent();
            cell.setcScale(Integer.parseInt(nextEvent.asCharacters().getData()));
          }
          case ZIP_FILE_LOCATION ->
          {
            nextEvent = reader.nextEvent();
            cell.setZipFileLocation(nextEvent.asCharacters().getData());
          }
          case PANEL ->
          {
            panel = new Panel();
          }
          case PANEL_NO ->
          {
            nextEvent = reader.nextEvent();
            panel.setPanelNumber(Integer.parseInt(nextEvent.asCharacters().getData()));
          }
          case VERTEX ->
          {
            coordinate = new Coordinate();
          }
          case LONG ->
          {
            nextEvent = reader.nextEvent();
            coordinate.x = Double.parseDouble(nextEvent.asCharacters().getData());
          }
          case LAT ->
          {
            nextEvent = reader.nextEvent();
            coordinate.y = Double.parseDouble(nextEvent.asCharacters().getData());
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
            panel.createGeom();
            cell.addPanel(panel);
          }
          case VERTEX -> panel.addVertex(coordinate);
        }
      }
    }

    return catalog;
  }

}