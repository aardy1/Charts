package org.knowtiphy.charts.settings;

public enum DepthUnit {
    M("m"),
    FEET("ft"),
    FATHOM("fth");

    private final String name;

    DepthUnit(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}