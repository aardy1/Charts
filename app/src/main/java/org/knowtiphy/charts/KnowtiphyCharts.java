package org.knowtiphy.charts;

import javafx.application.Application;
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
import org.knowtiphy.charts.chartview.ChartLockerDialog;
import org.knowtiphy.charts.chartview.ChartView;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.charts.desktop.AppSettingsDialog;
import org.knowtiphy.charts.dynamics.AISModel;
import org.knowtiphy.charts.enc.ChartLoader;
import org.knowtiphy.charts.enc.ChartLocker;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.charts.enc.SchemaAdapter;
import org.knowtiphy.charts.enc.event.ChartLockerEvent;
import org.knowtiphy.charts.memstore.MapStats;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.memstore.StyleReader;
import org.knowtiphy.charts.platform.IPlatform;
import org.knowtiphy.charts.platform.Platform;
import org.knowtiphy.charts.settings.AppSettings;
import org.knowtiphy.charts.utils.FXUtils;
import org.knowtiphy.charts.utils.ToggleModel;
import org.knowtiphy.shapemap.renderer.context.SVGCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.knowtiphy.charts.utils.FXUtils.later;
import static org.knowtiphy.charts.utils.FXUtils.resizeable;

public class KnowtiphyCharts extends Application
{
  // need to work these out from screen dimensions
  private static final int WIDTH = 1300;

  private static final int HEIGHT = 750;

  private static final int SETTINGS_WIDTH = 700;

  private static final int SETTINGS_HEIGHT = 400;

  private static final int CHART_LOCKER_WIDTH = 900;

  private static final int CHART_LOCKER_HEIGHT = 400;

  private static final SVGCache SVG_CACHE = new SVGCache(
    org.knowtiphy.charts.chartview.markicons.ResourceLoader.class);

  private ChartLocker chartLocker;

  private MapDisplayOptions displayOptions;

  private final AISModel dynamics = new AISModel();

  private ENCChart chart;

  private final BorderPane overlay = new BorderPane();

  private final AppSettings appSettings = new AppSettings();

  private final IPlatform platform = Platform.getPlatform();

  @Override
  public void start(Stage primaryStage) throws Exception
  {
    showInitialSetup(platform);

    var styleReader = new StyleReader<SimpleFeatureType, MemFeature>(ResourceLoader.class);
    var chartLoader = new ChartLoader(appSettings, styleReader);
    chartLocker = new ChartLocker(platform.catalogsDir(), platform.chartsDir(), chartLoader);

    displayOptions = new MapDisplayOptions();
    var cell = chartLocker.getCell("Gulf of Mexico", 2_160_000);
    chart = chartLocker.loadChart(cell.bounds(), cell.cScale() / 2.0, displayOptions, SVG_CACHE);

    chartLocker
      .chartEvents()
      .filter(ChartLockerEvent::isLoad)
      .subscribe(change -> setStageTitle(primaryStage, change.chart()));
    appSettings
      .unitProfile()
      .unitChangeEvents()
      .subscribe(change -> setStageTitle(primaryStage, chart));

    var stats = new MapStats(chart.maps(), SchemaAdapter.ADAPTER).stats();
    stats.print();

//    new Dump(chart, reader.getStore()).dump(S57.OC_BUAARE);

    // this won't be right after the info bar is done, but that will be resized later
    // chart.setViewPortScreenArea(new Rectangle2D(0, 0, width, height));

    var mapSurface = makeMap();

    var toggle = new ToggleModel();
    chartSpecificSettings(toggle);

    var infoBar = new InfoBar(toggle, chart, appSettings.unitProfile(), chartLocker, displayOptions,
      SVG_CACHE);

    var menuBar = mainMenuBar(primaryStage);

    var vbox = new VBox();
    vbox.getStyleClass().add("charts");
    VBox.setVgrow(mapSurface, Priority.ALWAYS);
    VBox.setVgrow(infoBar, Priority.NEVER);
    vbox.setFillWidth(true);
    vbox.getChildren().addAll(menuBar, mapSurface, infoBar);

    vbox.setPickOnBounds(false);
    overlay.setPickOnBounds(false);

    var scene = new Scene(new StackPane(vbox, overlay), WIDTH, HEIGHT);
    scene.getStylesheets().add(ResourceLoader.class.getResource("charts.css").toExternalForm());

    primaryStage.setScene(scene);
    setStageTitle(primaryStage, chart);
    primaryStage.sizeToScene();
    platform.setWindowIcons(primaryStage, ResourceLoader.class);
    primaryStage.show();
  }

  private ChartView makeMap()
  {
    return resizeable(
      new ChartView(chartLocker, chart, dynamics, appSettings.unitProfile(), displayOptions,
        SVG_CACHE));
  }

  private MenuBar mainMenuBar(Stage stage)
  {
    var menuBar = new MenuBar();
    menuBar.setUseSystemMenuBar(true);
    var menu = new Menu("Knowtiphy Charts");

    var items = new ArrayList<MenuItem>();

    var showSettings = new MenuItem("Settings");
    items.add(showSettings);
    showSettings.setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.META_DOWN));
    showSettings.setOnAction(x -> AppSettingsDialog
                                    .create(stage, SETTINGS_WIDTH, SETTINGS_HEIGHT, appSettings)
                                    .showAndWait());

    var showChartLocker = new MenuItem("Chart Locker");
    items.add(showChartLocker);
    showChartLocker.setOnAction(
      x -> new ChartLockerDialog(stage, chartLocker, displayOptions, SVG_CACHE)
             .create(CHART_LOCKER_WIDTH, CHART_LOCKER_HEIGHT)
             .showAndWait());

    if(platform.isMac())
    {
      stage.setOnCloseRequest(event -> shutdown());
    }
    else
    {
      var separatorNode = new HBox();
      separatorNode.setPadding(new Insets(5, 0, 0, 0));
      var separator = new SeparatorMenuItem();
      separator.setContent(separatorNode);
      items.add(separator);
      var quit = new MenuItem("Quit");
      items.add(quit);
      quit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.META_DOWN));
      quit.setOnAction(event -> shutdown());
    }

    menu.getItems().addAll(items);
    menuBar.getMenus().addAll(menu);

    return menuBar;
  }

  private void chartSpecificSettings(ToggleModel toggle)
  {
    var displayProperties = FXUtils.nonResizeable(
      new PropertySheet(displayOptions.getProperties()));
    BorderPane.setAlignment(displayProperties, Pos.CENTER);
    displayProperties.setOnMouseExited(evt -> toggle.toggle());
    toggle
      .getStateProperty()
      .addListener(cl -> later(() -> overlay.setRight(toggle.isOn() ? displayProperties : null)));
  }

  private void showInitialSetup(IPlatform platform)
  {
    System.err.println("Platform = " + platform.getClass().getCanonicalName());
    System.err.println("File System root = " + platform.rootDir());
    System.err.println("File System root exists = " + platform.rootDir().toFile().exists());
    System.err.println("Catalogs  dir = " + platform.catalogsDir());
    System.err.println("Catalogs  dir exists = " + platform.catalogsDir().toFile().exists());
    System.err.println("Charts  dir = " + platform.chartsDir());
    System.err.println("Charts  dir exists = " + platform.chartsDir().toFile().exists());

    try(var dave = Files.list(platform.chartsDir()))
    {
      var files = dave.map(Path::getFileName).map(Path::toString).toList();
      System.err.println("Files = " + files);
    }
    catch(IOException ex)
    {
      Logger.getLogger(KnowtiphyCharts.class.getName()).log(Level.SEVERE, null, ex);
    }

    System.err.println("Screen dimensions = " + platform.screenDimensions());
    System.err.println("Screen ppi = " + platform.ppi());
    System.err.println("Screen ppcm = " + platform.ppcm());
    System.err.println("Default font = " + Font.getDefault());
    // System.err.println("GPS Position = " + platform.positionProperty());

    platform.info();
    System.err.println();
  }

  private void setStageTitle(Stage stage, ENCChart chart)
  {
    if(chart.isQuilt())
    {
      platform.setStageTitle(stage,
        "Quilt %s             1::%d             %s".formatted(chart.title(), chart.cScale(),
          appSettings.unitProfile().formatEnvelope(chart.bounds())));
    }
    else
    {
      platform.setStageTitle(stage,
        "%s             1::%d             %s".formatted(chart.title(), chart.cScale(),
          appSettings.unitProfile().formatEnvelope(chart.bounds())));
    }
  }

  private void shutdown()
  {
    System.exit(1);
  }

  public static void main(String[] args)
  {
    Application.launch(KnowtiphyCharts.class, args);
  }

}

// private void fudgeKnowtiphyFunctionFactory() {
//
// var existingFactories = CommonFactoryFinder.getFunctionFactories(null);
// existingFactories.forEach(f -> {
// if (f instanceof KnowtiphyStyleFunctionsFactory ff) {
// ff.setDefautStyle(visuals);
// }
// });
// }
// var file = "/Users/graham/Desktop/enc map.txt";
// var file = "/Users/graham/Desktop/basic map.txt";

// Set categories = Collections.singleton(FunctionFactory.class);
// FactoryRegistry registry = new FactoryRegistry(categories);
// registry.registerFactory(new CRSProvider());
// ReferencingFactoryFinder.getCRSFactory(new Hints());
// ReferencingObjectFactory blah = new ReferencingObjectFactory();

//    var gf = new GeometryFactory();
//    var p1 = gf.createPoint(new Coordinate(100, 100));
//    var p2 = gf.createPoint(new Coordinate(200, 100));
//    var p3 = gf.createPoint(new Coordinate(300, 100));
//    var mp = gf.createMultiPoint(new Point[]{p1, p2, p3});
//
//    var pp1 = new Coordinate(50, 50);
//    var pp2 = new Coordinate(150, 50);
//    var pp3 = new Coordinate(150, 150);
//    var pp4 = new Coordinate(50, 150);
//    var pp5 = new Coordinate(50, 50);
//
//    var poly = gf.createPolygon(new Coordinate[]{pp1, pp2, pp3, pp4, pp5});
//
//    System.err.println("Poly contains p1 " + poly.contains(p1));
//    System.err.println("Poly contains p2 " + poly.contains(p2));
//    System.err.println("Poly contains p3 " + poly.contains(p3));
//    System.err.println("Poly contains mp " + poly.contains(mp));
//    System.err.println("Poly intersect mp " + poly.intersection(mp));
//    System.err.println("MP internal envelope " + mp.getEnvelopeInternal());
//    System.err.println("Poly internal envelope " + poly.getEnvelopeInternal());
//
//    var index = new STRtree();
//    index.insert(poly.getEnvelopeInternal(), poly);
//    index.insert(mp.getEnvelopeInternal(), mp);
//    index.insert(p1.getEnvelopeInternal(), p1);
//    index.insert(p2.getEnvelopeInternal(), p2);
//    index.insert(p3.getEnvelopeInternal(), p3);
//    var env = new ReferencedEnvelope(151, 152, 50, 150, DefaultEngineeringCRS.CARTESIAN_2D);
//    var res = index.query(env);
//    System.err.println("Res = " + res);

//    //  test for rendering speed with text on
//    new Thread(() -> {
//      try
//      {
//        Thread.sleep(35000);
//      }
//      catch(InterruptedException e)
//      {
//        throw new RuntimeException(e);
//      }
//      chart.setLayerVisible("SOUNDG", true);
//      int n = 1000;
//      for(int i = 0; i < n; i++)
//      {
//        runLater(() -> Coordinates.zoom(chart, 0.5));
//        runLater(() -> Coordinates.zoom(chart, 2));
//      }
//    }).start();