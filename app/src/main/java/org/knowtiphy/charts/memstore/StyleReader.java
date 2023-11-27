package org.knowtiphy.charts.memstore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import org.knowtiphy.shapemap.renderer.FeatureTypeStyle;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.style.parser.StyledLayerDescriptorParser;

/**
 * @author graham
 */
public class StyleReader {

	private static final String SLD = ".sld";

	private final Class<?> dir;

	public StyleReader(Class<?> dir) {
		this.dir = dir;
	}

	public FeatureTypeStyle createStyle(String fileName) throws IOException, XMLStreamException, StyleSyntaxException {
		var styleSheet = toSLDFile(fileName);
		assert styleSheet != null;
		return createFromSLD(styleSheet);
	}

	private FeatureTypeStyle createFromSLD(InputStream stream)
			throws IOException, XMLStreamException, StyleSyntaxException {
		return new StyledLayerDescriptorParser(stream).read();
	}

	private InputStream toSLDFile(String featureName) throws FileNotFoundException {
		System.err.println("styles/" + featureName + SLD);
		var dave = dir.getResourceAsStream("styles/" + featureName + SLD);
		System.err.println("stream = " + dave);
		return dave;
		// return dir.getResourceAsStream("styles/" + featureName + SLD);

		// var path = dir.resolve(featureName + SLD);
		// System.err.println("path = " + path);
		// return new FileInputStream(new File(path.toString()));
	}

}