package org.knowtiphy.charts.enc.event;

import org.knowtiphy.charts.enc.ENCChart;

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