/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.parser;

import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import shapemap.renderer.FeatureTypeStyle;
import shapemap.style.builder.FeatureTypeStyleBuilder;

import static shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class StyledLayerDescriptorParser {

	private final InputStream input;

	private final IParsingContext parsingContext;

	public StyledLayerDescriptorParser(InputStream input, IParsingContext parsingContext) {
		this.input = input;
		this.parsingContext = parsingContext;
	}

	public FeatureTypeStyle read() throws FileNotFoundException, XMLStreamException, StyleSyntaxException {

		var xmlInputFactory = XMLInputFactory.newInstance();
		var reader = xmlInputFactory.createXMLEventReader(input);

		var builder = new FeatureTypeStyleBuilder();

		var done = false;
		while (!done && reader.hasNext()) {
			var nextEvent = reader.nextTag();

			if (nextEvent.isStartElement()) {
				var startElement = nextEvent.asStartElement();
				switch (normalize(startElement)) {
					case XML.STYLED_LAYER_DESCRIPTOR, XML.USER_STYLE, XML.NAMED_LAYER, XML.FEATURE_TYPE_STYLE -> {
						// ignore
					}
					case XML.VENDOR_OPTION -> VendorOptionParser.parse(reader);
					case XML.FEATURE_TYPE_NAME -> builder.featureType(Utils.parseString(reader.nextEvent()));
					case XML.RULE -> builder.rule(RuleParser.parse(parsingContext, reader));
					default -> throw new IllegalArgumentException(startElement.toString());
				}
			}

			done = Utils.checkDone(nextEvent, XML.STYLED_LAYER_DESCRIPTOR);
		}

		return builder.build();
	}

}