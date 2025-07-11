/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser.symbolizer;

import org.knowtiphy.shapemap.api.IStyleCompiler;
import org.knowtiphy.shapemap.renderer.Operators;
import org.knowtiphy.shapemap.renderer.symbolizer.TextSymbolizer;
import org.knowtiphy.shapemap.style.builder.TextSymbolizerBuilder;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.style.parser.Utils;
import org.knowtiphy.shapemap.style.parser.VendorOptionParser;
import org.knowtiphy.shapemap.style.parser.XML;
import org.knowtiphy.shapemap.style.parser.basic.FillParser;
import org.knowtiphy.shapemap.style.parser.basic.FontParser;
import org.knowtiphy.shapemap.style.parser.basic.LabelPlacementParser;
import org.knowtiphy.shapemap.style.parser.basic.StrokeParser;
import org.knowtiphy.shapemap.style.parser.expression.ExpressionParser;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

import static org.knowtiphy.shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class TextSymbolizerParser
{

  public static <S, F> TextSymbolizer<S, F> parse(
    IStyleCompiler<F> parsingContext, XMLEventReader reader)
    throws XMLStreamException, StyleSyntaxException
  {

    var builder = new TextSymbolizerBuilder<S, F>();

    var done = false;
    while(!done && reader.hasNext())
    {
      var nextEvent = reader.nextTag();

      if(nextEvent.isStartElement())
      {
        var startElement = nextEvent.asStartElement();
        switch(normalize(startElement))
        {
          case XML.LABEL ->
          {
            var labelValue = ExpressionParser.parse(parsingContext, reader, XML.LABEL);
            builder.label((f, g) -> Operators.toString(labelValue, f, g));
          }
          case XML.FONT -> builder.font(FontParser.parse(reader));
          case XML.STROKE -> builder.strokeInfo(StrokeParser.parse(reader));
          case XML.FILL -> builder.fillInfo(FillParser.parse(reader));
          case XML.LABEL_PLACEMENT -> builder.labelPlacement(LabelPlacementParser.parse(reader));
          case XML.VENDOR_OPTION -> VendorOptionParser.parse(reader);
          default -> throw new IllegalArgumentException(startElement.toString());
        }
      }

      done = Utils.checkDone(nextEvent, XML.TEXT_SYMBOLIZER);
    }

    return builder.build();
  }

}