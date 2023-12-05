/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.parser;

import java.io.FileNotFoundException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import shapemap.renderer.symbolizer.basic.Rule;
import shapemap.style.builder.RuleBuilder;
import shapemap.style.parser.expression.ExpressionParser;
import shapemap.style.parser.symbolizer.LineSymbolizerParser;
import shapemap.style.parser.symbolizer.PointSymbolizerParser;
import shapemap.style.parser.symbolizer.PolygonSymbolizerParser;
import shapemap.style.parser.symbolizer.TextSymbolizerParser;

import static shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class RuleParser {

	public static Rule parse(IParsingContext parsingContext, XMLEventReader reader)
			throws FileNotFoundException, XMLStreamException, StyleSyntaxException {

		var builder = new RuleBuilder();

		var done = false;
		while (!done && reader.hasNext()) {
			var nextEvent = reader.nextTag();

			if (nextEvent.isStartElement()) {
				var startElement = nextEvent.asStartElement();
				switch (normalize(startElement)) {
					case XML.FILTER -> builder.filter(ExpressionParser.parse(parsingContext, reader, XML.FILTER));
					case XML.POINT_SYMBOLIZER ->
						builder.graphicSymbolizer(PointSymbolizerParser.parse(parsingContext, reader));
					case XML.LINE_SYMBOLIZER -> builder.graphicSymbolizer(LineSymbolizerParser.parse(reader));
					case XML.POLYGON_SYMBOLIZER -> builder.graphicSymbolizer(PolygonSymbolizerParser.parse(reader));
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