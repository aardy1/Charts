/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser.symbolizer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;
import org.knowtiphy.shapemap.style.builder.PointSymbolizerBuilder;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.style.parser.Utils;
import org.knowtiphy.shapemap.style.parser.VendorOptionParser;
import org.knowtiphy.shapemap.style.parser.XML;
import org.knowtiphy.shapemap.style.parser.expression.ExpressionParser;

import static org.knowtiphy.shapemap.style.parser.Utils.normalize;

import org.knowtiphy.shapemap.api.IStyleCompilerAdapter;

/**
 * @author graham
 */
public class PointSymbolizerParser {

	public static <S, F> ISymbolizer<S, F> parse(IStyleCompilerAdapter<F> parsingContext, XMLEventReader reader)
			throws XMLStreamException, StyleSyntaxException {

		var builder = new PointSymbolizerBuilder<S, F>();

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
					case XML.MARK -> builder.markSymbolizer(new MarkSymbolizerParser<S, F>().parse(reader));
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