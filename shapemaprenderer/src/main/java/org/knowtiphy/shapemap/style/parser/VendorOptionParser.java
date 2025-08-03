/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

/**
 * @author graham
 */
public class VendorOptionParser {

    public static void parse(XMLEventReader reader) throws XMLStreamException {

        var done = false;
        while (!done && reader.hasNext()) {
            var nextEvent = reader.nextEvent();
            // do nothing for the moment
            done = Utils.checkDone(nextEvent, XML.VENDOR_OPTION);
        }
    }
}