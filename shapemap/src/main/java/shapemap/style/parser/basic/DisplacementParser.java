/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.parser.basic;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import shapemap.renderer.symbolizer.basic.Displacement;
import shapemap.style.builder.DisplacementBuilder;
import shapemap.style.parser.Utils;
import shapemap.style.parser.XML;

import static shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class DisplacementParser {

	public static Displacement parse(XMLEventReader reader) throws XMLStreamException {

		var builder = new DisplacementBuilder();

		var done = false;
		while (!done && reader.hasNext()) {
			var nextEvent = reader.nextTag();
			if (nextEvent.isStartElement()) {
				var startElement = nextEvent.asStartElement();
				switch (normalize(startElement)) {
					case XML.DISPLACEMENTX -> builder.displacementX(Utils.parseDouble(reader.nextEvent()));
					case XML.DISPLACEMENTY -> builder.displacementY(Utils.parseDouble(reader.nextEvent()));
					default -> throw new IllegalArgumentException(startElement.toString());
				}
			}

			done = Utils.checkDone(nextEvent, XML.DISPLACEMENT);
		}

		return builder.build();
	}

}