package org.knowtiphy.charts.memstore;

import org.knowtiphy.shapemap.api.IStyleCompilerAdapter;
import org.knowtiphy.shapemap.renderer.FeatureTypeStyle;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.style.parser.StyledLayerDescriptorParser;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author graham
 */
public class StyleReader<S, F>
{
  //  TODO -- need to cache style sheets for re-use?
  
  private static final String SLD = ".sld";

  private final Class<?> resourceLoader;

  public StyleReader(Class<?> resourceLoader)
  {
    this.resourceLoader = resourceLoader;
  }

  public FeatureTypeStyle<S, F> createStyle(
    String fileName, IStyleCompilerAdapter<F> parsingContext)
    throws IOException, XMLStreamException, StyleSyntaxException
  {
    var styleSheet = toSLDFile(fileName);
    assert styleSheet != null;
    return createFromSLD(styleSheet, parsingContext);
  }

  private FeatureTypeStyle<S, F> createFromSLD(
    InputStream stream, IStyleCompilerAdapter<F> parsingContext)
    throws IOException, XMLStreamException, StyleSyntaxException
  {
    return new StyledLayerDescriptorParser<S, F>(stream, parsingContext).read();
  }

  private InputStream toSLDFile(String featureName)
  {
    return resourceLoader.getResourceAsStream("styles/" + featureName + SLD);
  }

}