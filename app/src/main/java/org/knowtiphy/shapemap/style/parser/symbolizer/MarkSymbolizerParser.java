/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser.symbolizer;

import java.util.Map;
import java.util.function.BiFunction;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.CircleMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.CrossMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.IMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.SquareMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.TriangleMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.XMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.knowtiphy.shapemap.style.builder.MarkSymbolizerBuilder;
import org.knowtiphy.shapemap.style.parser.Utils;
import org.knowtiphy.shapemap.style.parser.XML;
import org.knowtiphy.shapemap.style.parser.basic.FillParser;
import org.knowtiphy.shapemap.style.parser.basic.StrokeParser;

import static org.knowtiphy.shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class MarkSymbolizerParser {

	private static final Map<String, BiFunction<FillInfo, StrokeInfo, IMarkSymbolizer>> WKN = Map.of(
	//@formatter:off
			"circle", CircleMarkSymbolizer::new,
			"square", SquareMarkSymbolizer::new,
			"triangle", TriangleMarkSymbolizer::new,
			"cross", CrossMarkSymbolizer::new,
			"x", XMarkSymbolizer::new
	//  star
	);
	//@formatter:on

	public static IMarkSymbolizer parse(XMLEventReader reader) throws XMLStreamException {

		var builder = new MarkSymbolizerBuilder();

		var done = false;
		while (!done && reader.hasNext()) {
			var nextEvent = reader.nextTag();

			if (nextEvent.isStartElement()) {
				var startElement = nextEvent.asStartElement();

				switch (normalize(startElement)) {
					case XML.WELL_KNOWN_NAME ->
						builder.symbolizerBuilder(getMarkSymbolizer(Utils.parseString(reader.nextEvent())));
					case XML.FILL -> builder.fillInfo(FillParser.parse(reader));
					case XML.STROKE -> builder.strokeInfo(StrokeParser.parse(reader));
					default -> throw new IllegalArgumentException(startElement.toString());
				}
			}

			done = Utils.checkDone(nextEvent, XML.MARK);
		}

		return builder.build();
	}

	private static BiFunction<FillInfo, StrokeInfo, IMarkSymbolizer> getMarkSymbolizer(String name) {

		var markSymbolizer = WKN.get(name);
		if (markSymbolizer == null) {
			throw new IllegalArgumentException(name);
		}

		return markSymbolizer;

	}

}
