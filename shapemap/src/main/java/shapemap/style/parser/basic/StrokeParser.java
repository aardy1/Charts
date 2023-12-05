/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.parser.basic;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import shapemap.renderer.symbolizer.basic.StrokeInfo;
import shapemap.style.builder.StrokeInfoBuilder;
import shapemap.style.parser.Utils;
import shapemap.style.parser.XML;

import static shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class StrokeParser {

	public static StrokeInfo parse(XMLEventReader reader) throws XMLStreamException {

		var builder = new StrokeInfoBuilder();

		var done = false;
		while (!done && reader.hasNext()) {
			var nextEvent = reader.nextTag();

			if (nextEvent.isStartElement()) {
				var startElement = nextEvent.asStartElement();
				// TODO -- GraphicFill, GraphicStroke
				switch (normalize(startElement)) {
					case XML.CSS_PARAMETER -> {
						var iterator = startElement.getAttributes();
						while (iterator.hasNext()) {
							var attr = normalize(iterator.next());
							// TODO -- line joins and caps, dashes
							switch (attr) {
								case XML.CSS_STROKE -> builder.color(Utils.parseColor(reader.nextEvent()));
								case XML.CSS_STROKE_WIDTH -> builder.width(Utils.parseInt(reader.nextEvent()));
								case XML.CSS_STROKE_OPACITY -> builder.opacity(Utils.parseDouble(reader.nextEvent()));
								default -> throw new IllegalArgumentException(attr);
							}
						}
					}
					default -> throw new IllegalArgumentException(startElement.toString());
				}
			}

			done = Utils.checkDone(nextEvent, XML.STROKE);
		}

		return builder.build();
	}

}