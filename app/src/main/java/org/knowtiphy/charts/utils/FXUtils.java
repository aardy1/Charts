package org.knowtiphy.charts.utils;

import java.io.InputStream;
import java.util.List;
import java.util.function.BiConsumer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.lang3.tuple.Pair;

/** Collection of utility classes for JavaFX. */
public class FXUtils {

    private static final Text TEXT = new Text();

    public static void later(Runnable r) {
        Platform.runLater(r);
    }

    public static void setDockIcon(Stage stage, InputStream stream) {
        stage.getIcons().add(new Image(stream));
    }

    public static <T extends Region> T resizeable(T region) {
        region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return region;
    }

    public static <T extends Region> T nonResizeable(T region) {
        region.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        return region;
    }

    // this is still a bit hacky
    public static Tooltip tooltip(String text, Font font, int fontWidth, int width) {
        var prefWidth = Math.min((text.length() + 2) * fontWidth, width);
        var tooltip = new Tooltip(text);
        tooltip.setFont(font);
        tooltip.setShowDelay(Duration.millis(3));
        tooltip.setPrefWidth(prefWidth);
        tooltip.setWrapText(true);
        return tooltip;
    }

    public static Button button(
            String title, Node node, EventHandler<ActionEvent> eh, Tooltip tooltip) {
        var button = new Button(title, node);
        button.setTooltip(tooltip);
        button.setOnAction(eh);
        return button;
    }

    public static Button button(Node node, EventHandler<ActionEvent> eh, Tooltip tooltip) {
        return button("", node, eh, tooltip);
    }

    public static MenuButton menuButton(
            String title, Node node, List<MenuItem> items, Tooltip tooltip) {
        var button = new MenuButton(title, node);
        button.getItems().addAll(items);
        button.setTooltip(tooltip);
        return button;
    }

    public static MenuButton menuButton(Node node, List<MenuItem> items, Tooltip tooltip) {
        return menuButton("", node, items, tooltip);
    }

    public static ColumnConstraints neverGrow() {
        var constraint = new ColumnConstraints();
        constraint.setHgrow(Priority.NEVER);
        return constraint;
    }

    public static ColumnConstraints alwaysGrow() {
        var constraint = new ColumnConstraints();
        constraint.setHgrow(Priority.ALWAYS);
        constraint.setFillWidth(true);
        return constraint;
    }

    public static MenuBar systemMenuBar() {
        var menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);
        return menuBar;
    }

    public static Pair<Double, Double> textSize(Font font, String s) {
        TEXT.setText(s);
        TEXT.setFont(font);
        return Pair.of(TEXT.getBoundsInLocal().getWidth(), TEXT.getBoundsInLocal().getHeight());
    }

    public static Pair<Double, Double> textSize(Font font) {
        return textSize(font, "A");
    }

    public static Bounds textSizeFast(Font font, String s) {
        TEXT.setText(s);
        TEXT.setFont(font);
        return TEXT.getBoundsInLocal();
    }

    public static void addMousePressedHandler(Node node, EventHandler<MouseEvent> eh) {
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, eh);
    }

    public static void addDoubleClickHandler(Node node, EventHandler<MouseEvent> eh) {
        node.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                event -> {
                    if (event.getClickCount() > 1) {
                        eh.handle(event);
                    }
                });
    }

    public static void addZoomHandler(Node node, EventHandler<ZoomEvent> eh) {
        node.addEventHandler(ZoomEvent.ANY, eh);
    }

    public static void addDragHandler(Node node, BiConsumer<MouseEvent, DragState> eh) {
        org.knowtiphy.charts.utils.DragState dragState = new DragState();

        addMousePressedHandler(
                node,
                event -> {
                    dragState.startX = event.getSceneX();
                    dragState.startY = event.getSceneY();
                });

        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> eh.accept(event, dragState));
    }

    public static void addContextMenuHandler(Node node, EventHandler<MouseEvent> eh) {

        // windows on clicked, mac on pressed
        node.addEventHandler(
                MouseEvent.MOUSE_PRESSED,
                event -> {
                    if (event.isPopupTrigger()) eh.handle(event);
                });
        node.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                event -> {
                    if (event.isPopupTrigger()) eh.handle(event);
                });
    }

    public static Region spacer() {
        var region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }
}
