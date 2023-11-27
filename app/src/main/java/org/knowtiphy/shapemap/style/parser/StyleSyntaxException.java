/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser;

/**
 * @author graham
 */
public class StyleSyntaxException extends Exception {

	public StyleSyntaxException(String message) {
		super(message);
	}

	public static void expect(Object value, String message) throws StyleSyntaxException {
		if (value == null)
			throw new StyleSyntaxException(message);
	}

	public static void expectElement(Object value, String message) throws StyleSyntaxException {
		if (value == null)
			throw new StyleSyntaxException("Expected a " + message + " element");
	}

	public static void unknownValueFor(Object lookup, String supplied, String message) throws StyleSyntaxException {
		if (lookup == null)
			throw new StyleSyntaxException("Unknown value '" + supplied + "' for element " + message);
	}

}
