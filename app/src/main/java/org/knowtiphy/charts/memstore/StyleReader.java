package org.knowtiphy.charts.memstore;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import shapemap.renderer.FeatureTypeStyle;
import shapemap.renderer.api.IFeature;
import shapemap.style.parser.IParsingContext;
import shapemap.style.parser.StyleSyntaxException;
import shapemap.style.parser.StyledLayerDescriptorParser;

/**
 * @author graham
 */
public class StyleReader<F extends IFeature> {

	private static final String SLD = ".sld";

	private final Class<?> dir;

	public StyleReader(Class<?> dir) {
		this.dir = dir;
	}

	public FeatureTypeStyle<F> createStyle(String fileName, IParsingContext parsingContext)
			throws IOException, XMLStreamException, StyleSyntaxException {
		var styleSheet = toSLDFile(fileName);
		assert styleSheet != null;
		return createFromSLD(styleSheet, parsingContext);
	}

	private FeatureTypeStyle<F> createFromSLD(InputStream stream, IParsingContext parsingContext)
			throws IOException, XMLStreamException, StyleSyntaxException {
		return new StyledLayerDescriptorParser(stream, parsingContext).read();
	}

	private InputStream toSLDFile(String featureName) {
		return dir.getResourceAsStream("styles/" + featureName + SLD);
	}

}