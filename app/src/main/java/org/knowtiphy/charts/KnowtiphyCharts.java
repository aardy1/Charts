package org.knowtiphy.charts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;
import org.controlsfx.control.PropertySheet;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.chartlocker.ChartLocker;
import org.knowtiphy.charts.chartlocker.ENCCellLoader;
import org.knowtiphy.charts.chartview.ChartLockerDialog;
import org.knowtiphy.charts.chartview.ChartView;
import org.knowtiphy.charts.chartview.ChartViewModel;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.charts.chartview.MapViewport;
import org.knowtiphy.charts.chartview.markicons.MarkIconsResourceLoader;
import org.knowtiphy.charts.desktop.AppSettingsDialog;
import org.knowtiphy.charts.dynamics.AISModel;
import org.knowtiphy.charts.memstore.MapStats;
import org.knowtiphy.charts.memstore.StyleReader;
import org.knowtiphy.charts.platform.IUnderlyingPlatform;
import org.knowtiphy.charts.platform.UnderlyingPlatform;
import org.knowtiphy.charts.settings.AppSettings;
import org.knowtiphy.charts.settings.UnitProfile;
import org.knowtiphy.charts.utils.FXUtils;
import static org.knowtiphy.charts.utils.FXUtils.later;
import static org.knowtiphy.charts.utils.FXUtils.nonResizeable;
import static org.knowtiphy.charts.utils.FXUtils.resizeable;
import static org.knowtiphy.charts.utils.FXUtils.systemMenuBar;
import org.knowtiphy.charts.utils.ToggleModel;
import org.knowtiphy.shapemap.context.RemoveHolesFromPolygon;
import org.knowtiphy.shapemap.context.RenderGeomCache;
import org.knowtiphy.shapemap.context.SVGCache;

/** The Knowtiphy Charts application. */
public class KnowtiphyCharts extends Application {

    // TODO need to work these out from screen dimensions
    private static final int WIDTH = 1300;

    private static final int HEIGHT = 750;

    private static final int SETTINGS_WIDTH = 700;

    private static final int SETTINGS_HEIGHT = 400;

    private static final int CHART_LOCKER_WIDTH = 900;

    private static final int CHART_LOCKER_HEIGHT = 400;

    //  the platform we are running on
    private IUnderlyingPlatform platform;

    //  global options and settings
    private MapDisplayOptions displayOptions;
    private AppSettings appSettings;

    //  the global cache of SVG "images"
    private SVGCache svgCache;

    // the global chart locker
    private ChartLocker chartLocker;

    @Override
    public void start(Stage primaryStage) throws Exception {

        //  work out what platform we are running on and show initial platform info for debugging
        platform = UnderlyingPlatform.getPlatform();
        showInitialSetup(platform);

        //  create the global options
        displayOptions = new MapDisplayOptions();
        appSettings = new AppSettings(new UnitProfile());

        //  create the global svg cache
        svgCache = new SVGCache(MarkIconsResourceLoader.class);

        //  create the global chart locker
        chartLocker =
                new ChartLocker(
                        platform.catalogsDir(),
                        platform.chartsDir(),
                        new ENCCellLoader(new StyleReader<>(ResourceLoader.class)));

        //  load an initial chart  just for demos and dump its stats for debugging

        var cell = chartLocker.getCell("Gulf of Mexico", 2_160_000);
        var quilt =
                chartLocker.loadQuilt(
                        cell.bounds(), cell.cScale() / 2.0, appSettings, displayOptions);
        // this won't be right after the info bar is done, but that will be resized later
        var viewPort =
                new MapViewport(
                        cell.bounds(), new Rectangle2D(0, 0, WIDTH, HEIGHT), platform, false);

        var chart =
                new ChartViewModel(
                        quilt,
                        viewPort,
                        chartLocker,
                        appSettings,
                        displayOptions,
                        platform,
                        MemFeatureAdapter.ADAPTER,
                        new RemoveHolesFromPolygon(new RenderGeomCache()),
                        svgCache,
                        DefaultTextBoundsFunction.FUNCTION);

        var stats = new MapStats(chart.maps()).stats();
        stats.print();

        initGraphics(primaryStage, chart);
        registerListeners(primaryStage, chart);

        //  show the app
        primaryStage.sizeToScene();
        platform.setWindowIcons(primaryStage, ResourceLoader.class);
        primaryStage.show();
    }

    private void initGraphics(Stage primaryStage, ChartViewModel chart)
            throws NonInvertibleTransformException, TransformException {

        var dynamicsModel = new AISModel();

        //  the view of the current chart
        var chartView =
                resizeable(
                        new ChartView(
                                chartLocker,
                                chart,
                                dynamicsModel,
                                appSettings.unitProfile(),
                                displayOptions,
                                svgCache));

        //  the chart options pane that slides in and out when toggled on and off
        var toggle = new ToggleModel();
        var chartOptionsPane = chartOptionsPane(toggle);

        //  the info bar below the chart view
        var infoBar =
                FXUtils.resizeable(
                        new InfoBar(
                                toggle,
                                chart,
                                appSettings.unitProfile(),
                                chartLocker,
                                displayOptions,
                                svgCache));

        //  the main menu bar
        var menuBar = createMainMenuBar(primaryStage);

        //  the main content area -- the main menu above the chart view above the info bar
        var mainContent = new VBox();
        mainContent.getStyleClass().add("charts");
        VBox.setVgrow(chartView, Priority.ALWAYS);
        VBox.setVgrow(infoBar, Priority.NEVER);
        mainContent.setFillWidth(true);
        mainContent.getChildren().addAll(menuBar, chartView, infoBar);
        mainContent.setPickOnBounds(false);

        //  set the primary stage scene, title, size, and icons and show the primary stage
        var scene = new Scene(new StackPane(mainContent, chartOptionsPane), WIDTH, HEIGHT);
        scene.getStylesheets().add(ResourceLoader.class.getResource("charts.css").toExternalForm());
        primaryStage.setScene(scene);
        setStageTitle(primaryStage, chart);
    }

    private void registerListeners(Stage primaryStage, ChartViewModel chart) {
        //  when the app settings are changed update the primary stage title
        appSettings
                .unitProfile()
                .unitChangeEvents()
                .subscribe(_ -> setStageTitle(primaryStage, chart));
        chart.viewPortBoundsEvent().subscribe(_ -> setStageTitle(primaryStage, chart));
        chart.quiltChangeEvent().subscribe(_ -> setStageTitle(primaryStage, chart));
    }

    //  the chart specific options pane
    private BorderPane chartOptionsPane(ToggleModel toggle) {

        var options = new BorderPane();
        options.setPickOnBounds(false);

        var propertiesView = nonResizeable(new PropertySheet(displayOptions.getProperties()));
        BorderPane.setAlignment(propertiesView, Pos.CENTER);

        propertiesView.setOnMouseExited(evt -> toggle.toggle());
        toggle.getStateProperty()
                .addListener(
                        _ -> later(() -> options.setRight(toggle.isOn() ? propertiesView : null)));

        return options;
    }

    //  the main menu
    private Menu mainMenu(Stage stage) {

        var items = new ArrayList<MenuItem>();

        //  Settings entry on the main menu

        var showSettings = new MenuItem("Settings");
        //  TODO this should be platform dependent -- are settings COMMA on other platforms
        showSettings.setAccelerator(
                new KeyCodeCombination(KeyCode.COMMA, KeyCombination.META_DOWN));
        showSettings.setOnAction(
                _ ->
                        AppSettingsDialog.create(
                                        stage, SETTINGS_WIDTH, SETTINGS_HEIGHT, appSettings)
                                .showAndWait());
        items.add(showSettings);

        //  Chart Locker entry on the main menu

        var showChartLocker = new MenuItem("Chart Locker");
        showChartLocker.setOnAction(
                _ ->
                        new ChartLockerDialog(stage, chartLocker, displayOptions, svgCache)
                                .create(CHART_LOCKER_WIDTH, CHART_LOCKER_HEIGHT)
                                .showAndWait());
        items.add(showChartLocker);

        //  quit behavior

        if (platform.isMac()) {
            stage.setOnCloseRequest(event -> Platform.exit());
        } else {
            //  TODO -- what is this?
            var separatorNode = new HBox();
            separatorNode.setPadding(new Insets(5, 0, 0, 0));
            var separator = new SeparatorMenuItem();
            separator.setContent(separatorNode);
            items.add(separator);
            var quit = new MenuItem("Quit");
            items.add(quit);
            quit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.META_DOWN));
            quit.setOnAction(event -> Platform.exit());
        }

        var mainMenu = new Menu("Knowtiphy Charts");
        mainMenu.getItems().addAll(items);
        return mainMenu;
    }

    //  the main menu bar for the app
    private MenuBar createMainMenuBar(Stage stage) {
        var menuBar = systemMenuBar();
        menuBar.getMenus().addAll(mainMenu(stage));
        return menuBar;
    }

    //  set the title of the main stage
    private void setStageTitle(Stage stage, ChartViewModel chart) {

        platform.setStageTitle(
                stage,
                "%s             %s             %s"
                        .formatted(
                                chart.isQuilt() ? "Quilt " : chart.title(),
                                chart.isQuilt() ? "Multiple cScales " : "1::" + chart.cScale(),
                                appSettings.unitProfile().formatEnvelope(chart.viewPortBounds())));
    }

    public static void main(String[] args) {
        Application.launch(KnowtiphyCharts.class, args);
    }

    //  debugging stuff to show the inital platform setup
    private void showInitialSetup(IUnderlyingPlatform platform) throws IOException {

        System.err.println("Platform = " + platform.getClass().getCanonicalName());
        System.err.println("File System root = " + platform.rootDir());
        System.err.println("File System root exists = " + platform.rootDir().toFile().exists());
        System.err.println("Catalogs  dir = " + platform.catalogsDir());
        System.err.println("Catalogs  dir exists = " + platform.catalogsDir().toFile().exists());
        System.err.println("Charts  dir = " + platform.chartsDir());
        System.err.println("Charts  dir exists = " + platform.chartsDir().toFile().exists());

        try (var chartFiles = Files.list(platform.chartsDir())) {
            var files = chartFiles.map(Path::getFileName).map(Path::toString).toList();
            System.err.println("Files = " + files);
        }

        System.err.println("Screen dimensions = " + platform.screenDimensions());
        System.err.println("Screen ppi = " + platform.ppi());
        System.err.println("Screen ppcm = " + platform.ppcm());
        System.err.println("Default font = " + Font.getDefault());
        // System.err.println("GPS Position = " + platform.positionProperty());

        platform.info();
        System.err.println();
    }
}