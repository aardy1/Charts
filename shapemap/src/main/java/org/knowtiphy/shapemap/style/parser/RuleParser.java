/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser;

import org.knowtiphy.shapemap.api.IStyleCompiler;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Rule;
import org.knowtiphy.shapemap.style.builder.RuleBuilder;
import org.knowtiphy.shapemap.style.parser.expression.ExpressionParser;
import org.knowtiphy.shapemap.style.parser.symbolizer.LineSymbolizerParser;
import org.knowtiphy.shapemap.style.parser.symbolizer.PointSymbolizerParser;
import org.knowtiphy.shapemap.style.parser.symbolizer.PolygonSymbolizerParser;
import org.knowtiphy.shapemap.style.parser.symbolizer.TextSymbolizerParser;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;

import static org.knowtiphy.shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class RuleParser
{

  public static <S, F> Rule<S, F> parse(IStyleCompiler<F> parsingContext, XMLEventReader reader)
    throws FileNotFoundException, XMLStreamException, StyleSyntaxException
  {

    var builder = new RuleBuilder<S, F>();

    var done = false;
    while(!done && reader.hasNext())
    {
      var nextEvent = reader.nextTag();

      if(nextEvent.isStartElement())
      {
        var startElement = nextEvent.asStartElement();
        switch(normalize(startElement))
        {
          case XML.FILTER ->
            builder.filter(ExpressionParser.predicate(parsingContext, reader, XML.FILTER));
          case XML.POINT_SYMBOLIZER ->
            builder.graphicSymbolizer(PointSymbolizerParser.parse(parsingContext, reader));
          case XML.LINE_SYMBOLIZER -> builder.graphicSymbolizer(LineSymbolizerParser.parse(reader));
          case XML.POLYGON_SYMBOLIZER ->
            builder.graphicSymbolizer(PolygonSymbolizerParser.parse(reader));
          case XML.TEXT_SYMBOLIZER ->
            builder.textSymbolizer(TextSymbolizerParser.parse(parsingContext, reader));
          case XML.ELSE_FILTER -> builder.elseFilter();
          default -> throw new IllegalArgumentException(startElement.toString());
        }
      }

      done = Utils.checkDone(nextEvent, XML.RULE);
    }

    return builder.build();
  }

}