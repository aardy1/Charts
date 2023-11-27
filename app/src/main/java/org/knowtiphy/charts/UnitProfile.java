/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts;

import java.text.DecimalFormat;
import java.util.function.DoubleFunction;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * @author graham
 */
public class UnitProfile {

	public String distanceUnit = "km";

	public String speedUnit = "kph";

	public String depthUnit = "m";

	public String temperatureUnit = "C";

	public String screenUnit = "cm";

	public DoubleFunction<Double> fConvertDistance = x -> x;

	public DoubleFunction<Double> fConvertSpeed = x -> x;

	public DoubleFunction<Double> fConvertDepth = x -> x;

	public DoubleFunction<Double> fConvertTemperature = x -> x;

	public DoubleFunction<Double> fConvertScreenUnit = x -> x;

	public DoubleFunction<String> fLatLongString = x -> x + "";

	public double convertDistance(double d) {
		return fConvertDistance.apply(d);
	}

	public double convertSpeed(double d) {
		return fConvertSpeed.apply(d);
	}

	// e.g. if screen unit is inches, 1cm : 100km -> 1in : 254km
	public double convertFromScreenUnit(double d) {
		return fConvertScreenUnit.apply(d);
	}

	public static String labelLongitude(double value) {
		var df = new DecimalFormat("###.#\u00B0");
		return df.format(Math.abs(value)) + (value < 0 ? "W" : "E");
	}

	public String labelLattitude(double value) {
		var df = new DecimalFormat("###.#\u00B0");
		return df.format(Math.abs(value)) + (value < 0 ? "S" : "N");
	}

	public String envelopeLabel(ReferencedEnvelope bounds) {
		return labelLongitude(bounds.getMinX()) + "-" + labelLongitude(bounds.getMaxX()) + "   "
				+ labelLattitude(bounds.getMinY()) + "-" + labelLattitude(bounds.getMaxY());
	}

}
