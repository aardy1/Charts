/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser.symbolizer;

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

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import java.util.Map;
import java.util.function.BiFunction;

import static org.knowtiphy.shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */

// TODO -- this class is a mess
public class MarkSymbolizerParser<S, F> {

    private final Map<String, BiFunction<FillInfo, StrokeInfo, IMarkSymbolizer<S, F>>> WKN =
            Map.of(
                    // @formatter:off
                    "circle", CircleMarkSymbolizer::new,
                    "square", SquareMarkSymbolizer::new,
                    "triangle", TriangleMarkSymbolizer::new,
                    "cross", CrossMarkSymbolizer::new,
                    "x", XMarkSymbolizer::new
                    //  star
                    );
    // @formatter:on

    public final Map<String, BiFunction<FillInfo, StrokeInfo, IMarkSymbolizer<S, F>>> S52 =
            Map.of(
                    // @formatter:off
                    "Hazard-Lighthouse",
                    (f, s) -> new SVGMarkSymbolizer<>(new PathInfo("Hazard-Lighthouse.svg"), f, s),
                    "Hazard-Oil-Platform",
                    (f, s) ->
                            new SVGMarkSymbolizer<>(new PathInfo("Hazard-Oil-Platform.svg"), f, s),
                    "Hazard-Wreck",
                    (f, s) -> new SVGMarkSymbolizer<>(new PathInfo("Hazard-Wreck2.svg"), f, s),
                    "Arrow",
                    (f, s) -> new SVGMarkSymbolizer<>(new PathInfo("Arrow.svg"), f, s),
                    "Buoy",
                    (f, s) -> new SVGMarkSymbolizer<>(new PathInfo("Buoy.svg"), f, s),
                    "Beacon",
                    (f, s) -> new SVGMarkSymbolizer<>(new PathInfo("Beacon.svg"), f, s),
                    "Obstruction",
                    (f, s) -> new SVGMarkSymbolizer<>(new PathInfo("Obstruction.svg"), f, s),
                    "Rock",
                    (f, s) -> new SVGMarkSymbolizer<>(new PathInfo("Rock.svg"), f, s),
                    "Anchorage",
                    (f, s) -> new SVGMarkSymbolizer<>(new PathInfo("Anchorage.svg"), f, s),
                    "Radar-Beacon",
                    (f, s) -> new SVGMarkSymbolizer<>(new PathInfo("Radar-Beacon.svg"), f, s));

    // @formatter:on

    public IMarkSymbolizer<S, F> parse(XMLEventReader reader)
            throws XMLStreamException, StyleSyntaxException {

        var builder = new MarkSymbolizerBuilder<S, F>();

        var done = false;
        while (!done && reader.hasNext()) {
            var nextEvent = reader.nextTag();

            if (nextEvent.isStartElement()) {
                var startElement = nextEvent.asStartElement();

                switch (normalize(startElement)) {
                    case XML.WELL_KNOWN_NAME ->
                            builder.symbolizerBuilder(
                                    getMarkSymbolizer(
                                            Utils.parseString(reader.nextEvent()).trim()));
                    case XML.FILL -> builder.fillInfo(FillParser.parse(reader));
                    case XML.STROKE -> builder.strokeInfo(StrokeParser.parse(reader));
                    default -> throw new IllegalArgumentException(startElement.toString());
                }
            }

            done = Utils.checkDone(nextEvent, XML.MARK);
        }

        return builder.build();
    }

    private BiFunction<FillInfo, StrokeInfo, IMarkSymbolizer<S, F>> getMarkSymbolizer(String name)
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