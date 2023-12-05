/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.parser;

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
			throw new StyleSyntaxException("Expected a '" + message + "' element");
	}

	public static void expectElement(Object value1, Object value2, String message1, String message2)
			throws StyleSyntaxException {
		if (value1 == null && value2 == null)
			throw new StyleSyntaxException("Expected one of  '" + message1 + "' or '" + message2 + "' elements");
	}

	public static void unknownValueFor(Object lookup, String supplied, String message) throws StyleSyntaxException {
		if (lookup == null)
			throw new StyleSyntaxException("Unknown value '" + supplied + "' for element " + message);
	}

}
