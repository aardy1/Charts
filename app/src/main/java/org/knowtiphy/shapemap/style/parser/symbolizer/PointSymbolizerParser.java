/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser.symbolizer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import org.knowtiphy.shapemap.style.builder.PointSymbolizerBuilder;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.style.parser.Utils;
import org.knowtiphy.shapemap.style.parser.VendorOptionParser;
import org.knowtiphy.shapemap.style.parser.XML;
import org.knowtiphy.shapemap.style.parser.expression.ExpressionParser;
import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;

import static org.knowtiphy.shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class PointSymbolizerParser {

	public static ISymbolizer parse(XMLEventReader reader) throws XMLStreamException, StyleSyntaxException {

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
					case XML.SIZE ->
						builder.size(ExpressionParser.parseOrLiteral(reader, XML.SIZE, Utils::parseDouble));
					case XML.OPACITY -> builder.opacity(Utils.parseDouble(reader.nextEvent()));
					case XML.VENDOR_OPTION -> VendorOptionParser.parse(reader);
					default -> throw new IllegalArgumentException(startElement.toString());
				}
			}

			done = Utils.checkDone(nextEvent, XML.POINT_SYMBOLIZER);
		}

		return builder.build();
	}

}