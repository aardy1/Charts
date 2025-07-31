package org.knowtiphy.charts.settings;

public class AppSettings {
    private final UnitProfile unitProfile;

    public AppSettings() {
        this.unitProfile = new UnitProfile();
    }

    public UnitProfile unitProfile() {
        return unitProfile;
    }
}