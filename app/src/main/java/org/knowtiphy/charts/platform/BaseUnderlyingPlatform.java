/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.platform;

import java.nio.file.Path;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/** Base class for all abstractions of the underlying platform */
public abstract class BaseUnderlyingPlatform {
    private final Path root;

    // private final DisplayService display;
    //
    // private final PositionService positionService;

    BaseUnderlyingPlatform(Path root) {
        this.root = root;
        // display = Services.get(DisplayService.class).get();
        // var posService = Services.get(PositionService.class);
        // if (posService.isPresent()) {
        // positionService = posService.get();
        // positionService.start();
        // }
        // else
        // positionService = new DummyPositionService();
        // keep the compiler happy
    }

    public Path rootDir() {
        return root;
    }

    public Path chartsDir() {
        return rootDir().resolve(Path.of("ENC"));
    }

    public Path catalogsDir() {
        return rootDir().resolve(Path.of("ENC_Catalogs"));
    }

    public double ppi() {
        return Screen.getPrimary().getDpi();
    }

    public double ppcm() {
        return ppi() * 2.54;
    }

    public Rectangle2D screenDimensions() {
        return Screen.getPrimary().getBounds();
    }

    public double screenWidthCM() {
        var screen = screenDimensions();
        return screen.getWidth() / ppi() * 2.54;
    }

    public double screenHeightCM() {
        var screen = screenDimensions();
        return screen.getHeight() / ppi() * 2.54;
    }

    public double windowWidthCM(double regionWidth) {
        return screenWidthCM() * (regionWidth / screenDimensions().getWidth());
    }

    public double windowHeightCM(double height) {
        return screenHeightCM() * (height / screenDimensions().getHeight());
    }

    // public ReadOnlyObjectProperty<Position> positionProperty() {
    // return positionService.positionProperty();
    // }

    public void info() {
        System.err.println("Screen VB " + Screen.getPrimary().getVisualBounds());
        System.err.println("Screen Bounds " + Screen.getPrimary().getBounds());
        System.err.println("Screen Scale X " + Screen.getPrimary().getOutputScaleX());
        System.err.println("Screen Scale Y " + Screen.getPrimary().getOutputScaleY());
        // System.err.println("Display DD " + display.getDefaultDimensions());
        // System.err.println("Display SR " + display.getScreenResolution());
        // System.err.println("Display Scale " + display.getScreenScale());
        // System.err.println("Display Has Notch " + display.hasNotch());
        // System.err.println("Display Notch Property " + display.notchProperty());
        // System.err.println("Display Is Round " + display.isScreenRound());
        // System.err.println("Display is Phone " + display.isPhone());
        // System.err.println("Display is Tablet " + display.isTablet());
        // System.err.println("Display is Desktop " + display.isDesktop());
    }

    public boolean isMac() {
        return false;
    }
}