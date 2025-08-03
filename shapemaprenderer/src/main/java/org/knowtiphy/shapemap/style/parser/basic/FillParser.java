/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser.basic;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import org.knowtiphy.shapemap.renderer.symbolizer.basic.FillInfo;
import org.knowtiphy.shapemap.style.builder.FillInfoBuilder;
import org.knowtiphy.shapemap.style.parser.Utils;
import org.knowtiphy.shapemap.style.parser.XML;

import static org.knowtiphy.shapemap.style.parser.Utils.normalize;

/**
 * @author graham
 */
public class FillParser {

    public static FillInfo parse(XMLEventReader reader) throws XMLStreamException {

        var builder = new FillInfoBuilder();

        var done = false;
        while (!done && reader.hasNext()) {

            var nextEvent = reader.nextTag();

            if (nextEvent.isStartElement()) {
                var startElement = nextEvent.asStartElement();
                // TODO -- GraphicFill
                switch (normalize(startElement)) {
                    case XML.CSS_PARAMETER -> {
                        var iterator = startElement.getAttributes();
                        while (iterator.hasNext()) {
                            var attr = normalize(iterator.next());
                            switch (attr) {
                                case XML.CSS_FILL ->
                                        builder.fill(Utils.parseColor(reader.nextEvent()));
                                case XML.CSS_FILL_OPACITY ->
                                        builder.opacity(Utils.parseDouble(reader.nextEvent()));
                                default -> throw new IllegalArgumentException(attr);
                            }
                        }
                    }
                    default -> throw new IllegalArgumentException(startElement.toString());
                }
            }

            done = Utils.checkDone(nextEvent, XML.FILL);
        }

        return builder.build();
    }
}