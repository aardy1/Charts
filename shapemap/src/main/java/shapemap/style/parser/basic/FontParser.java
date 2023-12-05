/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.parser.basic;

import javafx.scene.text.Font;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import shapemap.style.builder.FontBuilder;
import shapemap.style.parser.StyleSyntaxException;
import shapemap.style.parser.Utils;
import shapemap.style.parser.XML;

import static shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class FontParser {

	public static Font parse(XMLEventReader reader) throws XMLStreamException, StyleSyntaxException {

		var builder = new FontBuilder();

		var done = false;
		while (!done && reader.hasNext()) {
			var nextEvent = reader.nextTag();

			if (nextEvent.isStartElement()) {
				var startElement = nextEvent.asStartElement();
				switch (normalize(startElement)) {
					case XML.CSS_PARAMETER -> {
						var iterator = startElement.getAttributes();
						while (iterator.hasNext()) {
							var attr = normalize(iterator.next());
							switch (attr) {
								case XML.CSS_FONT_FAMILY -> builder.family(Utils.parseFontFamily(reader.nextEvent()));
								case XML.CSS_FONT_SIZE -> builder.size(Utils.parseFontSize(reader.nextEvent()));
								case XML.CSS_FONT_WEIGHT -> builder.weight(Utils.parseFontWeight(reader.nextEvent()));
								case XML.CSS_FONT_STYLE -> builder.posture(Utils.parseFontPosture(reader.nextEvent()));
								default -> throw new IllegalArgumentException(attr);
							}
						}
					}
					default -> throw new IllegalArgumentException(startElement.toString());
				}
			}

			done = Utils.checkDone(nextEvent, XML.FONT);
		}

		return builder.build();
	}

}