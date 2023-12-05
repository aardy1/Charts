/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.parser.symbolizer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import shapemap.renderer.symbolizer.basic.FillInfo;
import shapemap.renderer.symbolizer.basic.PathInfo;
import shapemap.renderer.symbolizer.basic.StrokeInfo;
import shapemap.renderer.symbolizer.mark.CircleMarkSymbolizer;
import shapemap.renderer.symbolizer.mark.CrossMarkSymbolizer;
import shapemap.renderer.symbolizer.mark.IMarkSymbolizer;
import shapemap.renderer.symbolizer.mark.SVGMarkSymbolizer;
import shapemap.renderer.symbolizer.mark.SquareMarkSymbolizer;
import shapemap.renderer.symbolizer.mark.TriangleMarkSymbolizer;
import shapemap.renderer.symbolizer.mark.XMarkSymbolizer;
import shapemap.style.builder.MarkSymbolizerBuilder;
import shapemap.style.parser.StyleSyntaxException;
import shapemap.style.parser.Utils;
import shapemap.style.parser.XML;
import shapemap.style.parser.basic.FillParser;
import shapemap.style.parser.basic.StrokeParser;

import static shapemap.style.parser.Utils.normalize;

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

	public static final Map<String, BiFunction<FillInfo, StrokeInfo, IMarkSymbolizer>> S52 = new HashMap<>();
	static {
		S52.put("Hazard-Lighthouse", (f, s) -> new SVGMarkSymbolizer(new PathInfo("Hazard-Lighthouse.svg"), f, s));
		S52.put("Hazard-Oil-Platform", (f, s) -> new SVGMarkSymbolizer(new PathInfo("Hazard-Oil-Platform.svg"), f, s));
		S52.put("Hazard-Wreck", (f, s) -> new SVGMarkSymbolizer(new PathInfo("Hazard-Wreck2.svg"), f, s));
		S52.put("Arrow", (f, s) -> new SVGMarkSymbolizer(new PathInfo("Arrow.svg"), f, s));
	}

	public static IMarkSymbolizer parse(XMLEventReader reader) throws XMLStreamException, StyleSyntaxException {

		var builder = new MarkSymbolizerBuilder();

		var done = false;
		while (!done && reader.hasNext()) {
			var nextEvent = reader.nextTag();

			if (nextEvent.isStartElement()) {
				var startElement = nextEvent.asStartElement();

				switch (normalize(startElement)) {
					case XML.WELL_KNOWN_NAME ->
						builder.symbolizerBuilder(getMarkSymbolizer(Utils.parseString(reader.nextEvent()).trim()));
					case XML.FILL -> builder.fillInfo(FillParser.parse(reader));
					case XML.STROKE -> builder.strokeInfo(StrokeParser.parse(reader));
					default -> throw new IllegalArgumentException(startElement.toString());
				}
			}

			done = Utils.checkDone(nextEvent, XML.MARK);
		}

		return builder.build();
	}

	private static BiFunction<FillInfo, StrokeInfo, IMarkSymbolizer> getMarkSymbolizer(String name)
			throws StyleSyntaxException {

		var parts = name.split(":");

		if (parts.length == 1 || parts[0].equals("wkn")) {
			var markName = parts[parts.length - 1];
			var markSymbolizer = WKN.get(markName);
			if (markSymbolizer == null) {
				throw new IllegalArgumentException(name);
			}

			return markSymbolizer;
		}

		if (parts[0].equals("s52")) {
			var markName = parts[parts.length - 1];
			var markSymbolizer = S52.get(markName);
			if (markSymbolizer == null) {
				throw new IllegalArgumentException(name);
			}

			return markSymbolizer;
		}

		throw new StyleSyntaxException("Don't recognize mark name : " + name);
	}

}
