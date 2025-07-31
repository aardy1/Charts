/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.settings;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.utils.Utils;
import org.reactfx.EventSource;
import org.reactfx.EventStream;

import java.util.function.UnaryOperator;

import static org.knowtiphy.charts.settings.SpeedUnit.KNOTS;

/**
 * @author graham
 */
public class UnitProfile {
    private static final String N = " N";
    private static final String S = " S";
    private static final String W = " W";
    private static final String E = " E";
    private static final String DEG_MIN_SEC = "%02d\u00B0%02d’%02d” %s";

    private final EventSource<Boolean> unitChangeEvents = new EventSource<>();

    public final ObjectProperty<SpeedUnit> speedUnit = new SimpleObjectProperty<>(KNOTS);
    public final ObjectProperty<DistanceUnit> distanceUnit =
            new SimpleObjectProperty<>(DistanceUnit.KM);
    public final ObjectProperty<DepthUnit> depthUnit = new SimpleObjectProperty<>(DepthUnit.M);
    public final ObjectProperty<TemperatureUnit> temperatureUnit =
            new SimpleObjectProperty<>(TemperatureUnit.C);

    public final IntegerProperty speedUnitDecimals = new SimpleIntegerProperty(2);
    public final IntegerProperty distanceUnitDecimals = new SimpleIntegerProperty(2);
    public final IntegerProperty depthUnitDecimals = new SimpleIntegerProperty(2);
    public final IntegerProperty temperatureUnitDecimals = new SimpleIntegerProperty(2);

    public final ObjectProperty<LatLongFormat> latLongFormat =
            new SimpleObjectProperty<>(LatLongFormat.DECIMAL_DEGREES);

    public UnitProfile() {
        speedUnit.addListener(this::unitChange);
        speedUnitDecimals.addListener(this::unitChange);
        distanceUnit.addListener(this::unitChange);
        distanceUnitDecimals.addListener(this::unitChange);
        depthUnit.addListener(this::unitChange);
        depthUnitDecimals.addListener(this::unitChange);
        temperatureUnit.addListener(this::unitChange);
        temperatureUnitDecimals.addListener(this::unitChange);
        latLongFormat.addListener(this::unitChange);
    }

    public EventStream<Boolean> unitChangeEvents() {
        return unitChangeEvents;
    }

    public DistanceUnit distanceUnit() {
        return distanceUnit.get();
    }

    public SpeedUnit speedUnit() {
        return speedUnit.get();
    }

    public DepthUnit depthUnit() {
        return depthUnit.get();
    }

    public TemperatureUnit temperatureUnit() {
        return temperatureUnit.get();
    }

    public String formatDistance(Number value, UnaryOperator<Number> converter) {
        return Utils.formatDecimal(converter.apply(value), distanceUnitDecimals.getValue())
                + " "
                + distanceUnit.get();
    }

    public String formatSpeed(Number value, UnaryOperator<Number> converter) {
        return Utils.formatDecimal(converter.apply(value), speedUnitDecimals.getValue());
    }

    public String formatDepth(Number value, UnaryOperator<Number> converter) {
        return Utils.formatDecimal(converter.apply(value), depthUnitDecimals.getValue());
    }

    public String formatTemperature(Number value, UnaryOperator<Number> converter) {
        return Utils.formatDecimal(converter.apply(value), temperatureUnitDecimals.getValue());
    }

    public String formatLongitude(double value) {
        var direction = value < 0 ? W : E;
        return switch (latLongFormat.get()) {
            case DECIMAL_DEGREES -> Utils.formatDecimal(value, 2) + direction;
            case DEGREES_MINUTES_SECONDS -> degMinSec(value, direction);
        };
    }

    public String formatLatitude(double value) {
        var direction = value < 0 ? S : N;
        return switch (latLongFormat.get()) {
            case DECIMAL_DEGREES -> Utils.formatDecimal(value, 2) + direction;
            case DEGREES_MINUTES_SECONDS -> degMinSec(value, direction);
        };
    }

    public String formatEnvelope(ReferencedEnvelope envelope) {
        return formatLatitude(envelope.getMinY())
                + " : "
                + formatLatitude(envelope.getMaxY())
                + " / "
                + formatLongitude(envelope.getMinX())
                + " : "
                + formatLongitude(envelope.getMaxX());
    }

    // e.g. if screen unit is inches, 1cm : 100km -> 1in : 254km
    //  public double convertFromScreenUnit(double d)
    //  {
    //    return fConvertScreenUnit.apply(d);
    //  }

    //  actual conversion functions

    public Number metersToMapUnits(Number value) {
        return switch (distanceUnit.get()) {
            case KM -> value.doubleValue() / 1000;
            case M -> value;
            case NM -> value.doubleValue() * 0.0005399568;
        };
    }

    public Number depthToMapUnits(Number value) {
        return switch (depthUnit.get()) {
            case M -> value;
            case FEET -> value.doubleValue() * 3.281;
            case FATHOM -> value.doubleValue() * 0.5468066492;
        };
    }

    public Number knotsToMapUnits(Number value) {
        return switch (speedUnit.get()) {
            case KPH -> value.doubleValue() * 1.852;
            case KNOTS -> value;
        };
    }

    private <T> void unitChange(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        unitChangeEvents.push(true);
    }

    private String degMinSec(double value, String direction) {
        var val = Math.abs(value);
        var degrees = (int) Math.floor(val);
        var remainder = val - degrees;
        val = remainder * 60;
        var minutes = (int) Math.floor(val);
        remainder = val - minutes;
        var seconds = (int) Math.floor(remainder * 60);
        return DEG_MIN_SEC.formatted(degrees, minutes, seconds, direction);
    }
}