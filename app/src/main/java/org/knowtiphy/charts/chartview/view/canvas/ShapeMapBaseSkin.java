package org.knowtiphy.charts.chartview.view.canvas;

import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;

public abstract class ShapeMapBaseSkin<F> extends SkinBase<ShapeMapControl<F>>
        implements Skin<ShapeMapControl<F>> {

    private static final double MINIMUM_WIDTH = 8;

    private static final double MINIMUM_HEIGHT = 8;

    private static final double MAXIMUM_WIDTH = Double.MAX_VALUE;

    private static final double MAXIMUM_HEIGHT = Double.MAX_VALUE;

    protected ShapeMapBaseSkin(ShapeMapControl<F> surface) {
        super(surface);
    }

    @Override
    protected double computeMinWidth(
            final double height,
            final double top,
            final double right,
            final double bottom,
            final double left) {
        return MINIMUM_WIDTH;
    }

    @Override
    protected double computeMinHeight(
            final double width,
            final double top,
            final double right,
            final double bottom,
            final double left) {
        return MINIMUM_HEIGHT;
    }

    @Override
    protected double computeMaxWidth(
            final double width,
            final double top,
            final double right,
            final double bottom,
            final double left) {
        return MAXIMUM_WIDTH;
    }

    @Override
    protected double computeMaxHeight(
            final double width,
            final double top,
            final double right,
            final double bottom,
            final double left) {
        return MAXIMUM_HEIGHT;
    }

    protected ShapeMapControl<F> S() {
        return getSkinnable();
    }
}