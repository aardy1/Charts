/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser;

import javafx.scene.paint.Color;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import static org.knowtiphy.shapemap.style.parser.StyleSyntaxException.unknownValueFor;

/**
 * @author graham
 */
public class Utils {

    public static int parseInt(XMLEvent nextEvent) {
        return Integer.parseInt(nextEvent.asCharacters().getData());
    }

    public static double parseDouble(XMLEvent nextEvent) {
        return Double.parseDouble(nextEvent.asCharacters().getData());
    }

    public static String parseString(XMLEvent nextEvent) {
        return nextEvent.asCharacters().getData();
    }

    public static Object parseLiteral(XMLEvent nextEvent) {

        try {
            return Utils.parseInt(nextEvent);
        } catch (NumberFormatException e1) {
            // ignore
        }

        try {
            return Utils.parseDouble(nextEvent);
        } catch (NumberFormatException e2) {
            // ignore
        }

        return Utils.parseString(nextEvent);
    }

    public static Color parseColor(XMLEvent nextEvent) {
        return Color.web(nextEvent.asCharacters().getData());
    }

    public static String parseFontFamily(XMLEvent nextEvent) {
        return parseString(nextEvent);
    }

    public static int parseFontSize(XMLEvent nextEvent) {
        return parseInt(nextEvent);
    }

    public static FontWeight parseFontWeight(XMLEvent nextEvent) throws StyleSyntaxException {
        var wght = nextEvent.asCharacters().getData().strip();
        var weight = FontWeight.findByName(wght);
        unknownValueFor(weight, wght, "font weight");
        return weight;
    }

    public static FontPosture parseFontPosture(XMLEvent nextEvent) throws StyleSyntaxException {
        var style = nextEvent.asCharacters().getData().strip();
        // CSS uses normal, JavaFX uses regular
        if (style.equals("normal")) {
            style = "regular";
        }
        // CSS has italic and oblique, and they are different, JavaFX only has italic
        if (style.equals("oblique")) {
            style = "italic";
        }
        var posture = FontPosture.findByName(style);
        unknownValueFor(posture, style, "font style");
        return posture;
    }

    public static String normalize(StartElement startElement) {
        return startElement.getName().getLocalPart().toLowerCase();
    }

    public static String normalize(EndElement endElement) {
        return endElement.getName().getLocalPart().toLowerCase();
    }

    public static String normalize(Attribute attribute) {
        return attribute.getValue().toLowerCase();
    }

    public static String normalizeKey(Attribute attribute) {
        return attribute.getName().getLocalPart().toLowerCase();
    }

    public static boolean checkDone(XMLEvent event, String tag) {
        return event.isEndElement() && normalize(event.asEndElement()).equals(tag);
    }
}