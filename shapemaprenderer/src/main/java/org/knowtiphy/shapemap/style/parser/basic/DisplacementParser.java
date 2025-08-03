/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser.basic;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.Displacement;
import org.knowtiphy.shapemap.style.builder.DisplacementBuilder;
import org.knowtiphy.shapemap.style.parser.Utils;
import org.knowtiphy.shapemap.style.parser.XML;

import static org.knowtiphy.shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class DisplacementParser {

    public static Displacement parse(XMLEventReader reader) throws XMLStreamException {

        var builder = new DisplacementBuilder();

        var done = false;
        while (!done && reader.hasNext()) {
            var nextEvent = reader.nextTag();
            if (nextEvent.isStartElement()) {
                var startElement = nextEvent.asStartElement();
                switch (normalize(startElement)) {
                    case XML.DISPLACEMENTX ->
                            builder.displacementX(Utils.parseDouble(reader.nextEvent()));
                    case XML.DISPLACEMENTY ->
                            builder.displacementY(Utils.parseDouble(reader.nextEvent()));
                    default -> throw new IllegalArgumentException(startElement.toString());
                }
            }

            done = Utils.checkDone(nextEvent, XML.DISPLACEMENT);
        }

        return builder.build();
    }
}