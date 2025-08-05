package org.knowtiphy.charts.chartlocker.event;

import org.knowtiphy.charts.chartview.ChartViewModel;

public record ChartLockerEvent(Type type, ChartViewModel chart) {
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