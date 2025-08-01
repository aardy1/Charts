package org.knowtiphy.charts.chart.event;

import org.knowtiphy.charts.chart.ENCChart;

public record ChartLockerEvent(Type type, ENCChart chart) {
    public enum Type {
        LOADED,
        UNLOADED
    }

    public boolean isUnload() {
        return type == Type.UNLOADED;
    }

    public boolean isLoad() {
        return type == Type.LOADED;
    }
}