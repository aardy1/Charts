/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.parser.symbolizer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import shapemap.renderer.symbolizer.ISymbolizer;
import shapemap.style.builder.PolygonSymbolizerBuilder;
import shapemap.style.parser.StyleSyntaxException;
import shapemap.style.parser.Utils;
import shapemap.style.parser.VendorOptionParser;
import shapemap.style.parser.XML;
import shapemap.style.parser.basic.FillParser;
import shapemap.style.parser.basic.StrokeParser;

import static shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class PolygonSymbolizerParser {

	public static ISymbolizer parse(XMLEventReader reader) throws XMLStreamException, StyleSyntaxException {

		var builder = new PolygonSymbolizerBuilder();

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