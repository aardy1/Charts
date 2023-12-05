/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

/**
 * @author graham
 */
public class Functions {

	public static final Map<String, Function<Object, Object>> builtinUnary = new HashMap<>();
	static {
		builtinUnary.put("getZ", Functions::getZ);
		builtinUnary.put("geometryType", Functions::geometryType);
	}

	public static final Map<String, Function<List<Object>, Object>> builtins = new HashMap<>();
	static {
		builtins.put("getZ", Functions::getZ);
		builtins.put("geometryType", Functions::geometryType);
	}

	public static Function<Object, Object> builtinUnary(String name) {
		var function = builtinUnary.get(name);

		if (function == null)
			throw new IllegalArgumentException(name);

		return function;
	}

	public static Function<List<Object>, Object> builtin(String name) {
		var function = builtins.get(name);

		if (function == null)
			throw new IllegalArgumentException(name);

		return function;
	}

	private static Object getZ(Object arg) {
		var geom = (Geometry) arg;
		return switch (geom.getGeometryType()) {
			case "Point" -> ((Point) geom).getCoordinate().getZ();
			default -> throw new IllegalArgumentException(arg.toString());
		};
	}

	private static Object geometryType(Object arg) {
		var geom = (Geometry) arg;
		return geom.getGeometryType();
	}

}
