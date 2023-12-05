/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.parser.symbolizer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import shapemap.renderer.Operators;
import shapemap.renderer.symbolizer.TextSymbolizer;
import shapemap.renderer.api.IFeature;
import shapemap.style.builder.TextSymbolizerBuilder;
import shapemap.style.parser.IParsingContext;
import shapemap.style.parser.StyleSyntaxException;
import shapemap.style.parser.Utils;
import shapemap.style.parser.VendorOptionParser;
import shapemap.style.parser.XML;
import shapemap.style.parser.basic.FillParser;
import shapemap.style.parser.basic.FontParser;
import shapemap.style.parser.basic.LabelPlacementParser;
import shapemap.style.parser.basic.StrokeParser;
import shapemap.style.parser.expression.ExpressionParser;

import static shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class TextSymbolizerParser {

	public static <F extends IFeature> TextSymbolizer parse(IParsingContext parsingContext, XMLEventReader reader)
			throws XMLStreamException, StyleSyntaxException {

		var builder = new TextSymbolizerBuilder<F>();

		var done = false;
		while (!done && reader.hasNext()) {
			var nextEvent = reader.nextTag();

			if (nextEvent.isStartElement()) {
				var startElement = nextEvent.asStartElement();
				switch (normalize(startElement)) {
					case XML.LABEL -> {
						var labelValue = ExpressionParser.<F>parse(parsingContext, reader, XML.LABEL);
						builder.label((f, g) -> Operators.toString(labelValue, f, g));
					}
					case XML.FONT -> builder.font(FontParser.parse(reader));
					case XML.STROKE -> builder.strokeInfo(StrokeParser.parse(reader));
					case XML.FILL -> builder.fillInfo(FillParser.parse(reader));
					case XML.LABEL_PLACEMENT -> builder.labelPlacement(LabelPlacementParser.parse(reader));
					case XML.VENDOR_OPTION -> VendorOptionParser.parse(reader);
					default -> throw new IllegalArgumentException(startElement.toString());
				}
			}

			done = Utils.checkDone(nextEvent, XML.TEXT_SYMBOLIZER);
		}

		return builder.build();
	}

}