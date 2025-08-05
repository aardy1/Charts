package org.knowtiphy.charts.chartview.view.canvas;

import java.util.List;
import java.util.Objects;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import org.knowtiphy.charts.chartview.shapemapview.IShapeMapViewModel;

/**
 * A shape map view -- a control that shows an ESRI shape map of layers of features of some schema
 * type.
 *
 * @param <S> the type of the schema
 * @param <F> the type of the features
 */
public class ShapeMapControl<S, F> extends Control {

    public enum SkinType {
        CANVAS
    }

    // CSS styling
    private static final StyleablePropertyFactory<ShapeMapControl<?, ?>> FACTORY =
            new StyleablePropertyFactory<>(Control.getClassCssMetaData());

    private static String DEFAULT_STYLE_SHEET;

    private final SkinType skinType;

    private final IShapeMapViewModel<S, F> viewModel;

    private final Color background;

    public ShapeMapControl(IShapeMapViewModel<S, F> viewModel, Color background) {
        this.viewModel = viewModel;
        this.background = background;
        this.skinType = SkinType.CANVAS;
        getStyleClass().add("shapemap-view");
    }

    @Override
    protected Skin<ShapeMapControl<S, F>> createDefaultSkin() {
        return switch (skinType) {
            default -> new CanvasShapeMapSkin<>(this, viewModel, background);
        };
    }

    @Override
    public synchronized String getUserAgentStylesheet() {

        switch (skinType) {
            case CANVAS:
            default:
                if (DEFAULT_STYLE_SHEET == null) {
                    DEFAULT_STYLE_SHEET =
                            Objects.requireNonNull(
                                            ShapeMapResourceLoader.class.getResource("canvas.css"))
                                    .toExternalForm();
                }
                return DEFAULT_STYLE_SHEET;
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return FACTORY.getCssMetaData();
    }
}

//    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData()
//    {
//        return FACTORY.getCssMetaData();
//    }