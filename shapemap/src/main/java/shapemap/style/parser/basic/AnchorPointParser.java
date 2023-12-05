/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.parser.basic;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import shapemap.renderer.symbolizer.basic.AnchorPoint;
import shapemap.style.builder.AnchorPointBuilder;
import shapemap.style.parser.Utils;
import shapemap.style.parser.XML;

import static shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class AnchorPointParser {

	public static AnchorPoint parse(XMLEventReader reader) throws XMLStreamException {

		var builder = new AnchorPointBuilder();

		var done = false;
		while (!done && reader.hasNext()) {
			var nextEvent = reader.nextTag();

			if (nextEvent.isStartElement()) {
				var startElement = nextEvent.asStartElement();
				switch (normalize(startElement)) {
					case XML.ANCHOR_POINTX -> builder.anchorX(Utils.parseDouble(reader.nextEvent()));
					case XML.ANCHOR_POINTY -> builder.anchorY(Utils.parseDouble(reader.nextEvent()));
					default -> throw new IllegalArgumentException(startElement.toString());
				}
			}

			done = Utils.checkDone(nextEvent, XML.ANCHOR_POINT);
		}

		return builder.build();
	}

}