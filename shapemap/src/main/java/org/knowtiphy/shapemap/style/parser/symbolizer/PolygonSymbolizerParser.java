/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser.symbolizer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import org.knowtiphy.shapemap.renderer.symbolizer.ISymbolizer;
import org.knowtiphy.shapemap.style.builder.PolygonSymbolizerBuilder;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.style.parser.Utils;
import org.knowtiphy.shapemap.style.parser.VendorOptionParser;
import org.knowtiphy.shapemap.style.parser.XML;
import org.knowtiphy.shapemap.style.parser.basic.FillParser;
import org.knowtiphy.shapemap.style.parser.basic.StrokeParser;

import static org.knowtiphy.shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class PolygonSymbolizerParser {

	public static <S, F> ISymbolizer<S, F> parse(XMLEventReader reader)
			throws XMLStreamException, StyleSyntaxException {

		var builder = new PolygonSymbolizerBuilder<S, F>();

		var done = false;
		while (!done && reader.hasNext()) {
			var nextEvent = reader.nextTag();

			if (nextEvent.isStartElement()) {
				var startElement = nextEvent.asStartElement();
				switch (normalize(startElement)) {
					case XML.FILL -> builder.fillInfo(FillParser.parse(reader));
					case XML.STROKE -> builder.strokeInfo(StrokeParser.parse(reader));
					case XML.VENDOR_OPTION -> VendorOptionParser.parse(reader);
					default -> throw new IllegalArgumentException(startElement.toString());
				}
			}

			done = Utils.checkDone(nextEvent, XML.POLYGON_SYMBOLIZER);
		}

		return builder.build();
	}

}