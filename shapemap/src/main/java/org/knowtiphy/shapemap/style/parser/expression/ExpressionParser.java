/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.style.parser.expression;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.api.IFeatureFunction;
import org.knowtiphy.shapemap.api.IParsingContext;
import org.knowtiphy.shapemap.renderer.Functions;
import org.knowtiphy.shapemap.renderer.Operators;
import org.knowtiphy.shapemap.style.parser.Utils;

import static org.knowtiphy.shapemap.style.parser.Utils.normalize;
import static org.knowtiphy.shapemap.style.parser.Utils.normalizeKey;

/**
 * @author graham
 */
public class ExpressionParser {

	public static <F extends IFeature> IFeatureFunction<F, ?> parse(IParsingContext<F> parsingContext,
			XMLEventReader reader, String finishTag) throws XMLStreamException {

		var stack = new LinkedList<LinkedList<IFeatureFunction<F, ?>>>();
		startFrame(stack);

		var done = false;
		while (!done && reader.hasNext()) {

			var nextEvent = reader.nextTag();

			if (nextEvent.isStartElement()) {
				var startElement = nextEvent.asStartElement();
				switch (normalize(startElement)) {

					case org.knowtiphy.shapemap.style.parser.XML.LITERAL -> {
						var value = Utils.parseLiteral(reader.nextEvent());
						push(stack, (feature, geom) -> value);
					}
					case org.knowtiphy.shapemap.style.parser.XML.PROPERTY_NAME -> {
						var name = Utils.parseString(reader.nextEvent());
						if (name.equals("the_geom"))
							push(stack, (feature, geom) -> geom);
						else {
							var propertyAccess = parsingContext.compilePropertyAccess(name);
							push(stack, propertyAccess);
						}
					}
					case org.knowtiphy.shapemap.style.parser.XML.FUNCTION -> {
						startFrame(stack);
						var fName = functionName(startElement);
						push(stack, (feature, geom) -> fName);
					}
					case org.knowtiphy.shapemap.style.parser.XML.EQ, org.knowtiphy.shapemap.style.parser.XML.NE,
							org.knowtiphy.shapemap.style.parser.XML.LT, org.knowtiphy.shapemap.style.parser.XML.GT,
							org.knowtiphy.shapemap.style.parser.XML.IS_LIKE,
							org.knowtiphy.shapemap.style.parser.XML.COALESCE ->
						startFrame(stack);
					default -> {
						throw new IllegalArgumentException(normalize(startElement));
					}
				}
			}

			if (nextEvent.isEndElement()) {
				var endElement = nextEvent.asEndElement();
				var tag = normalize(endElement);
				switch (tag) {

					case org.knowtiphy.shapemap.style.parser.XML.LITERAL,
							org.knowtiphy.shapemap.style.parser.XML.PROPERTY_NAME -> {
						// do nothing
					}

					case org.knowtiphy.shapemap.style.parser.XML.FUNCTION ->
						push(stack, makeFunctionCall(endFrame(stack)));

					case org.knowtiphy.shapemap.style.parser.XML.EQ -> push(stack,
							mkBop(endFrame(stack), (l, r) -> (feature, geom) -> Operators.eq(l, r, feature, geom)));
					case org.knowtiphy.shapemap.style.parser.XML.NE -> push(stack,
							mkBop(endFrame(stack), (l, r) -> (feature, geom) -> Operators.ne(l, r, feature, geom)));
					case org.knowtiphy.shapemap.style.parser.XML.LT -> push(stack,
							mkBop(endFrame(stack), (l, r) -> (feature, geom) -> Operators.lt(l, r, feature, geom)));
					case org.knowtiphy.shapemap.style.parser.XML.GT -> push(stack,
							mkBop(endFrame(stack), (l, r) -> (feature, geom) -> Operators.gt(l, r, feature, geom)));
					case org.knowtiphy.shapemap.style.parser.XML.IS_LIKE -> push(stack,
							mkBop(endFrame(stack), (l, r) -> (feature, geom) -> Operators.like(l, r, feature, geom)));
					case org.knowtiphy.shapemap.style.parser.XML.COALESCE -> push(stack, mkBop(endFrame(stack),
							(l, r) -> (feature, geom) -> Operators.coalesce(l, r, feature, geom)));
					default -> {
						if (tag.equals(finishTag))
							done = true;
						else
							throw new IllegalArgumentException(normalize(endElement));
					}
				}
			}
		}

		assert stack.size() == 1 : stack.size();
		assert stack.peek().size() == 1 : stack.peek().size();

		return endFrame(stack).pop();
	}

	public static <F extends IFeature, T> IFeatureFunction<F, T> parseOrLiteral(IParsingContext<F> parsingContext,
			XMLEventReader reader, String finishTag, Function<XMLEvent, T> literalParser) throws XMLStreamException {

		// this is a bit hacky
		try {
			var value = literalParser.apply(reader.peek());
			reader.nextEvent();
			return (f, g) -> value;
		}
		catch (NumberFormatException ex) {
			return (IFeatureFunction<F, T>) parse(parsingContext, reader, finishTag);
		}
	}

	private static <F extends IFeature> void startFrame(LinkedList<LinkedList<IFeatureFunction<F, ?>>> stack) {
		stack.push(new LinkedList<>());
	}

	private static <F extends IFeature> LinkedList<IFeatureFunction<F, ?>> endFrame(
			LinkedList<LinkedList<IFeatureFunction<F, ?>>> stack) {
		return stack.pop();
	}

	private static <F extends IFeature> void push(LinkedList<LinkedList<IFeatureFunction<F, ?>>> stack,
			IFeatureFunction<F, ?> function) {
		stack.peek().push(function);
	}

	private static String functionName(StartElement startElement) throws XMLStreamException {

		String name = null;
		var iterator = startElement.getAttributes();
		while (iterator.hasNext()) {
			var attribute = iterator.next();
			var key = normalizeKey(attribute);
			switch (key) {
				case org.knowtiphy.shapemap.style.parser.XML.ATTR_NAME -> {
					name = attribute.getValue();
				}
				default -> throw new IllegalArgumentException(key);
			}
		}

		return name;
	}

	private static <F extends IFeature> IFeatureFunction<F, Object> makeFunctionCall(
			LinkedList<IFeatureFunction<F, ?>> frame) throws XMLStreamException {

		int size = frame.size();
		var funArgs = new ArrayList<IFeatureFunction<F, ?>>();
		for (int i = 0; i < size - 1; i++) {
			funArgs.add(frame.pop());
		}

		try {
			// TODO -- optimize the likes code
			var function = Functions.builtinUnary((String) frame.pop().apply(null, null));
			return (feature, geom) -> function.apply(funArgs.get(0).apply(feature, geom));
		}
		catch (IllegalArgumentException e) {
			// it's not a unary function
		}

		var function = Functions.builtin((String) frame.pop().apply(null, null));

		return (feature, geom) -> {
			var args = new ArrayList<>();
			for (var arg : funArgs) {
				args.add(arg.apply(feature, geom));
			}

			return function.apply(args);
		};
	}

	private static <F extends IFeature> IFeatureFunction<F, ?> mkBop(LinkedList<IFeatureFunction<F, ?>> frame,
			BiFunction<IFeatureFunction<F, ?>, IFeatureFunction<F, ?>, IFeatureFunction<F, ?>> operator)
			throws XMLStreamException {

		var right = frame.pop();
		var left = frame.pop();
		return operator.apply(left, right);
	}

}