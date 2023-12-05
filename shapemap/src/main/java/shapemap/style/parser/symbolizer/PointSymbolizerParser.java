/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.parser.symbolizer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import shapemap.renderer.symbolizer.ISymbolizer;
import shapemap.style.builder.PointSymbolizerBuilder;
import shapemap.style.parser.IParsingContext;
import shapemap.style.parser.StyleSyntaxException;
import shapemap.style.parser.Utils;
import shapemap.style.parser.VendorOptionParser;
import shapemap.style.parser.XML;
import shapemap.style.parser.expression.ExpressionParser;

import static shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class PointSymbolizerParser {

	public static ISymbolizer parse(IParsingContext parsingContext, XMLEventReader reader)
			throws XMLStreamException, StyleSyntaxException {

		var builder = new PointSymbolizerBuilder();

		var done = false;
		while (!done && reader.hasNext()) {
			var nextEvent = reader.nextTag();

			if (nextEvent.isStartElement()) {
				var startElement = nextEvent.asStartElement();

				// TODO -- rotations, external graphics, expressions
				switch (normalize(startElement)) {
					case XML.GRAPHIC -> {
						// ignore
					}
					case XML.MARK -> builder.markSymbolizer(MarkSymbolizerParser.parse(reader));
					case XML.SIZE -> builder.size(
							ExpressionParser.parseOrLiteral(parsingContext, reader, XML.SIZE, Utils::parseDouble));
					case XML.OPACITY -> builder.opacity(Utils.parseDouble(reader.nextEvent()));
					case XML.ROTATION -> builder.rotation(
							ExpressionParser.parseOrLiteral(parsingContext, reader, XML.ROTATION, Utils::parseDouble));
					case XML.VENDOR_OPTION -> VendorOptionParser.parse(reader);
					default -> throw new IllegalArgumentException(startElement.toString());
				}
			}

			done = Utils.checkDone(nextEvent, XML.POINT_SYMBOLIZER);
		}

		return builder.build();
	}

}