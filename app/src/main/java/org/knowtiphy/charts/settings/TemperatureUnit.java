package org.knowtiphy.charts.settings;

public enum TemperatureUnit {
    C("C"),
    F("F");

    final String name;

    private TemperatureUnit(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}