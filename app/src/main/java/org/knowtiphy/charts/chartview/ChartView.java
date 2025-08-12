package org.knowtiphy.charts.chartview;

import java.util.List;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import org.knowtiphy.charts.chartlocker.ChartLocker;
import org.knowtiphy.charts.dynamics.AISModel;
import org.knowtiphy.charts.settings.UnitProfile;

/** A control to show (quilts of) ENC charts. */
public class ChartView extends Control {

    public enum SkinType {
        //  will have a multi-canvas view too?
        CANVAS
    }

    // CSS stuff
    private static final StyleablePropertyFactory<ChartView> FACTORY =
            new StyleablePropertyFactory<>(Control.getClassCssMetaData());

    private StyleableProperty<Color> color =
            new SimpleStyleableObjectProperty<>(
                    FACTORY.createColorCssMetaData("-color", s -> s.color, Color.RED, false),
                    this,
                    "color");

    private static String DEFAULT_STYLE_SHEET;

    private SkinType skinType;

    private final ChartLocker chartLocker;

    private final ChartViewModel chart;

    private final AISModel dynamics;

    private final UnitProfile unitProfile;

    private final MapDisplayOptions displayOptions;

    public ChartView(
            ChartLocker chartLocker,
            ChartViewModel chart,
            AISModel dynamics,
            UnitProfile unitProfile,
            MapDisplayOptions displayOptions) {
        this(chartLocker, chart, dynamics, unitProfile, displayOptions, SkinType.CANVAS);
    }

    public ChartView(
            ChartLocker chartLocker,
            ChartViewModel map,
            AISModel dynamics,
            UnitProfile unitProfile,
            MapDisplayOptions displayOptions,
            SkinType skinType) {

        this.chartLocker = chartLocker;
        this.chart = map;
        this.dynamics = dynamics;
        this.unitProfile = unitProfile;
        this.displayOptions = displayOptions;

        this.skinType = skinType;
        getStyleClass().add("chartview");

        // publish events for changes in the dynamics
        //        eventModel.boatUpdates.feedFrom(dynamics.aisEvents);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    protected Skin createDefaultSkin() {
        return new ChartViewSkin(this, chartLocker, chart, dynamics, unitProfile, displayOptions);
    }

    @Override
    public synchronized String getUserAgentStylesheet() {
        switch (skinType) {
            case CANVAS:
            default:
                if (DEFAULT_STYLE_SHEET == null) {
                    DEFAULT_STYLE_SHEET =
                            org.knowtiphy.charts.chartview.ChartViewResourceLoader.class
                                    .getResource("chartview.css")
                                    .toExternalForm();
                }
                return DEFAULT_STYLE_SHEET;
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return FACTORY.getCssMetaData();
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return FACTORY.getCssMetaData();
    }
    //
    //    public class EventModel {
    //
    //        public final EventSource<MouseEvent> mouseEvents = new EventSource<>();
    //
    //        public final EventStream<MouseEvent> mousePressed =
    //                mouseEvents.filter(event -> event.getEventType() == MouseEvent.MOUSE_PRESSED);
    //
    //        public final EventStream<MouseEvent> mouseReleased =
    //                mouseEvents.filter(event -> event.getEventType() ==
    // MouseEvent.MOUSE_RELEASED);
    //
    //        public final EventStream<MouseEvent> mouseClicked =
    //                mouseEvents.filter(event -> event.getEventType() == MouseEvent.MOUSE_CLICKED);
    //
    //        public final EventStream<MouseEvent> mouseDoubleClicked =
    //                mouseEvents.filter(
    //                        event ->
    //                                event.getEventType() == MouseEvent.MOUSE_CLICKED
    //                                        && event.getClickCount() > 1);
    //
    //        public final EventStream<MouseEvent> mouseDragStarted =
    //                mouseEvents.filter(event -> event.getEventType() == MouseEvent.DRAG_DETECTED);
    //
    //        public final EventStream<MouseEvent> mouseDragged =
    //                mouseEvents.filter(event -> event.getEventType() == MouseEvent.MOUSE_DRAGGED);
    //
    //        public final EventSource<ScrollEvent> scrollEvents = new EventSource<>();
    //
    //        public final EventSource<ZoomEvent> zoomEvents = new EventSource<>();
    //
    //        public final EventSource<AISEvent> boatUpdates = new EventSource<>();
    //    }
}