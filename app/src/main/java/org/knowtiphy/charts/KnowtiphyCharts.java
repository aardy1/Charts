package org.knowtiphy.charts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.stage.Stage;
import org.controlsfx.control.PropertySheet;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.knowtiphy.charts.chart.ChartLoader;
import org.knowtiphy.charts.chart.ChartLocker;
import org.knowtiphy.charts.chart.ENCChart;
import org.knowtiphy.charts.chart.event.ChartLockerEvent;
import org.knowtiphy.charts.chartview.ChartLockerDialog;
import org.knowtiphy.charts.chartview.ChartView;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.charts.chartview.markicons.MarkIconsResourceLoader;
import org.knowtiphy.charts.desktop.AppSettingsDialog;
import org.knowtiphy.charts.dynamics.AISModel;
import org.knowtiphy.charts.memstore.MapStats;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.memstore.StyleReader;
import org.knowtiphy.charts.platform.IUnderlyingPlatform;
import org.knowtiphy.charts.platform.UnderlyingPlatform;
import org.knowtiphy.charts.settings.AppSettings;
import org.knowtiphy.charts.settings.UnitProfile;
import org.knowtiphy.charts.utils.FXUtils;
import static org.knowtiphy.charts.utils.FXUtils.later;
import static org.knowtiphy.charts.utils.FXUtils.resizeable;
import static org.knowtiphy.charts.utils.FXUtils.systemMenuBar;
import org.knowtiphy.charts.utils.ToggleModel;
import org.knowtiphy.shapemap.context.SVGCache;

/** The Knowtiphy Charts application. */
public class KnowtiphyCharts extends Application {

    // need to work these out from screen dimensions
    private static final int WIDTH = 1300;

    private static final int HEIGHT = 750;

    private static final int SETTINGS_WIDTH = 700;

    private static final int SETTINGS_HEIGHT = 400;

    private static final int CHART_LOCKER_WIDTH = 900;

    private static final int CHART_LOCKER_HEIGHT = 400;

    //  the global cache of SVG "images"
    private final SVGCache svgCache = new SVGCache(MarkIconsResourceLoader.class);

    //  global options and settings (creating the MapDisplayOptions can throw an exception so is
    // done in the main method)
    private MapDisplayOptions displayOptions;
    private final AppSettings appSettings = new AppSettings(new UnitProfile());

    //  the platform we are running on
    private final IUnderlyingPlatform platform = UnderlyingPlatform.getPlatform();

    // the global chart locker
    private ChartLocker chartLocker;

    @Override
    public void start(Stage primaryStage) throws Exception {

        //  show initial platform info for debugging
        showInitialSetup(platform);

        //  create global display options
        displayOptions = new MapDisplayOptions();

        //  create the global chart locker
        var styleReader = new StyleReader<SimpleFeatureType, MemFeature>(ResourceLoader.class);
        var chartLoader = new ChartLoader(appSettings, styleReader);
        chartLocker =
                new ChartLocker(
                        platform.catalogsDir(), platform.chartsDir(), chartLoader, displayOptions);

        //  load an initial chart  just for demos
        var cell = chartLocker.getCell("Gulf of Mexico", 2_160_000);
        var chart = chartLocker.loadChart(cell.bounds(), cell.cScale() / 2.0, svgCache);

        //  dump the charts stats for debugging
        var stats = new MapStats(chart.maps()).stats();
        stats.print();

        // this won't be right after the info bar is done, but that will be resized later
        // chart.setViewPortScreenArea(new Rectangle2D(0, 0, width, height));

        //  the view of the current chart
        var dynamicsModel = new AISModel();
        var chartView =
                resizeable(
                        new ChartView(
                                chartLocker,
                                chart,
                                dynamicsModel,
                                appSettings.unitProfile(),
                                displayOptions,
                                svgCache));

        //  when a new chart is loaded update the primary stage title
        chartLocker
                .chartEvents()
                .filter(ChartLockerEvent::isLoad)
                .subscribe(change -> setStageTitle(primaryStage, change.chart()));

        //  when the app settings are changed update the primary stage title
        appSettings
                .unitProfile()
                .unitChangeEvents()
                .subscribe(change -> setStageTitle(primaryStage, chart));

        //  the chart options pane that slides in and out when toggled on and off
        var toggle = new ToggleModel();
        var chartOptionsPane = chartOptionsPane(toggle);

        //  the info bar below the chart view
        var infoBar =
                new InfoBar(
                        toggle,
                        chart,
                        appSettings.unitProfile(),
                        chartLocker,
                        displayOptions,
                        svgCache);

        //  main menu bar
        var menuBar = mainMenuBar(primaryStage);

        //  main content area -- the main menu above the chart view above the info bar
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
        primaryStage.sizeToScene();
        platform.setWindowIcons(primaryStage, ResourceLoader.class);
        primaryStage.show();
    }

    //  the chart specific options pane
    private BorderPane chartOptionsPane(ToggleModel toggle) {

        var options = new BorderPane();
        options.setPickOnBounds(false);

        var propertiesView =
                FXUtils.nonResizeable(new PropertySheet(displayOptions.getProperties()));
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
        //  TODO this should be platform dependent -- are settings COMMA on other platforms

        var showSettings = new MenuItem("Settings");
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
                x ->
                        new ChartLockerDialog(stage, chartLocker, displayOptions, svgCache)
                                .create(CHART_LOCKER_WIDTH, CHART_LOCKER_HEIGHT)
                                .showAndWait());
        items.add(showChartLocker);

        //  quit behavior

        if (platform.isMac()) {
            stage.setOnCloseRequest(event -> Platform.exit());
        } else {
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
    private MenuBar mainMenuBar(Stage stage) {
        var menuBar = systemMenuBar();
        menuBar.getMenus().addAll(mainMenu(stage));
        return menuBar;
    }

    //  set the title of the main stage
    private void setStageTitle(Stage stage, ENCChart chart) {

        platform.setStageTitle(
                stage,
                "%s%s             1::%d             %s"
                        .formatted(
                                chart.isQuilt() ? "Quilt " : "",
                                chart.title(),
                                chart.cScale(),
                                appSettings.unitProfile().formatEnvelope(chart.bounds())));
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