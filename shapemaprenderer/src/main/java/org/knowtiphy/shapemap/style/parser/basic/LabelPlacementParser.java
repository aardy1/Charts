/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser.basic;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.LabelPlacement;
import org.knowtiphy.shapemap.style.builder.LabelPlacementBuilder;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.style.parser.Utils;
import org.knowtiphy.shapemap.style.parser.XML;

import static org.knowtiphy.shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class LabelPlacementParser {

    public static LabelPlacement parse(XMLEventReader reader)
            throws XMLStreamException, StyleSyntaxException {

        var builder = new LabelPlacementBuilder();

        var done = false;
        while (!done && reader.hasNext()) {
            var nextEvent = reader.nextTag();
            if (nextEvent.isStartElement()) {
                var startElement = nextEvent.asStartElement();
                switch (normalize(startElement)) {
                    case XML.POINT_PLACEMENT ->
                            builder.pointPlacement(PointPlacementParser.parse(reader));
                    default -> throw new IllegalArgumentException(startElement.toString());
                }
            }

            done = Utils.checkDone(nextEvent, XML.LABEL_PLACEMENT);
        }

        return builder.build();
    }
}