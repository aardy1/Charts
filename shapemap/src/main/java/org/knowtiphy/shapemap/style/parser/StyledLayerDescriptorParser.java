/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser;

import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.api.IParsingContext;
import org.knowtiphy.shapemap.renderer.FeatureTypeStyle;
import org.knowtiphy.shapemap.style.builder.FeatureTypeStyleBuilder;

import static org.knowtiphy.shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class StyledLayerDescriptorParser<S, F extends IFeature> {

	private final InputStream input;

	private final IParsingContext<F> parsingContext;

	public StyledLayerDescriptorParser(InputStream input, IParsingContext<F> parsingContext) {
		this.input = input;
		this.parsingContext = parsingContext;
	}

	public FeatureTypeStyle<S, F> read() throws FileNotFoundException, XMLStreamException, StyleSyntaxException {

		var xmlInputFactory = XMLInputFactory.newInstance();
		var reader = xmlInputFactory.createXMLEventReader(input);

		var builder = new FeatureTypeStyleBuilder<S, F>();

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