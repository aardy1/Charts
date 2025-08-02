package org.knowtiphy.charts.settings;

public enum SpeedUnit {
    KPH("kph"),
    KNOTS("knots");

    private final String name;

    private SpeedUnit(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}