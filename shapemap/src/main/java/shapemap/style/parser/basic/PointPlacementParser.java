/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.parser.basic;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import shapemap.renderer.symbolizer.basic.PointPlacement;
import shapemap.style.builder.PointPlacementBuilder;
import shapemap.style.parser.StyleSyntaxException;
import shapemap.style.parser.Utils;
import shapemap.style.parser.XML;

import static shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class PointPlacementParser {

	public static PointPlacement parse(XMLEventReader reader) throws XMLStreamException, StyleSyntaxException {

		var builder = new PointPlacementBuilder();

		var done = false;
		while (!done && reader.hasNext()) {
			var nextEvent = reader.nextTag();
			if (nextEvent.isStartElement()) {
				var startElement = nextEvent.asStartElement();
				switch (normalize(startElement)) {
					case XML.ANCHOR_POINT -> builder.anchorPoint(AnchorPointParser.parse(reader));
					case XML.DISPLACEMENT -> builder.displacement(DisplacementParser.parse(reader));
					default -> throw new IllegalArgumentException(startElement.toString());
				}
			}

			done = Utils.checkDone(nextEvent, XML.POINT_PLACEMENT);
		}

		return builder.build();
	}

}