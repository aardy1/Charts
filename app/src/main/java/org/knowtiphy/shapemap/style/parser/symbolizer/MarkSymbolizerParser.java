/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser.symbolizer;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import org.girod.javafx.svgimage.SVGLoader;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.PathInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.StrokeInfo;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.CircleMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.CrossMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.IMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.SVGMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.SquareMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.TriangleMarkSymbolizer;
import org.knowtiphy.shapemap.renderer.symbolizer.mark.XMarkSymbolizer;
import org.knowtiphy.shapemap.style.builder.MarkSymbolizerBuilder;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
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

	private static final Map<String, BiFunction<FillInfo, StrokeInfo, IMarkSymbolizer>> S52 = new HashMap<>();
	static {
		try {
			var image = SVGLoader.load(new File("/Users/graham/Desktop/foo.svg").toURI().toURL());
			S52.put("plane", (f, s) -> new SVGMarkSymbolizer(new PathInfo("plane", image), f, s));
		}
		catch (MalformedURLException ex) {
			Logger.getLogger(CircleMarkSymbolizer.class.getName()).log(Level.SEVERE, null, ex);

		}
	}
	//@formatter:on

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
