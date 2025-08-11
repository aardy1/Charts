package org.knowtiphy.charts.memstore;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import org.knowtiphy.shapemap.api.IStyleCompiler;
import org.knowtiphy.shapemap.renderer.FeatureTypeStyle;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.style.parser.StyledLayerDescriptorParser;

/**
 * @author graham
 */
public class StyleReader<F> {
    //  TODO -- need to cache style sheets for re-use?

    private static final String SLD = ".sld";

    private final Class<?> resourceLoader;

    public StyleReader(Class<?> resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public FeatureTypeStyle<F> createStyle(String fileName, IStyleCompiler<F> parsingContext)
            throws IOException, XMLStreamException, StyleSyntaxException {
        var styleSheet = toSLDFile(fileName);
        assert styleSheet != null;
        return createFromSLD(styleSheet, parsingContext);
    }

    private FeatureTypeStyle<F> createFromSLD(InputStream stream, IStyleCompiler<F> parsingContext)
            throws IOException, XMLStreamException, StyleSyntaxException {
        return new StyledLayerDescriptorParser<F>(stream, parsingContext).read();
    }

    private InputStream toSLDFile(String featureName) {
        return resourceLoader.getResourceAsStream("styles/" + featureName + SLD);
    }
}