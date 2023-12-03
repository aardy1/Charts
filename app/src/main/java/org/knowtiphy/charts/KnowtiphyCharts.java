package org.knowtiphy.charts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.control.PropertySheet;
import org.knowtiphy.charts.chartview.ChartView;
import org.knowtiphy.charts.dynamics.AISModel;
import org.knowtiphy.charts.enc.CatalogReader;
import org.knowtiphy.charts.enc.ChartLocker;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.charts.enc.LocalChartProvider;
import org.knowtiphy.charts.memstore.MapStats;
import org.knowtiphy.charts.memstore.StyleReader;
import org.knowtiphy.charts.platform.IPlatform;
import org.knowtiphy.charts.platform.Platform;
import org.knowtiphy.charts.utils.FXUtils;
import org.knowtiphy.charts.utils.ToggleModel;
import org.knowtiphy.shapemap.viewmodel.MapDisplayOptions;

import static org.knowtiphy.charts.utils.FXUtils.later;
import static org.knowtiphy.charts.utils.FXUtils.resizeable;

public class KnowtiphyCharts extends Application {

	// need to work these out from screen dimensions
	private final int width = 1300;

	private final int height = 750;

	private final UnitProfile unitProfile = new UnitProfile();

	private ChartLocker chartLocker;

	private MapDisplayOptions displayOptions;

	private final AISModel dynamics = new AISModel();

	private ENCChart chart;

	private final BorderPane overlay = new BorderPane();

	@Override
	public void start(Stage primaryStage) throws Exception {

		var platform = Platform.getPlatform();

		// var styleDir = Platform.getStylesPath();
		showInitialSetup(platform);

		var catalogFile = platform.chartsDir().resolve("08Region_ENCProdCat.xml");// 08Region_ENCProdCat.xml");
		var catalog = new CatalogReader(catalogFile).read();

		// var styleReader = new StyleReader(styleDir);
		var styleReader = new StyleReader(getClass());
		var chartProvider = new LocalChartProvider(catalog, platform.chartsDir(), styleReader);
		chartLocker = new ChartLocker(chartProvider);
		var chartDescription = chartProvider.getChartDescription("Gulf of Mexico", 2_160_000);

		displayOptions = new MapDisplayOptions();
		chart = chartLocker.getChart(chartDescription, displayOptions);
		var stats = new MapStats(chart).stats();
		stats.print();

		// new Dump(mapContent, reader.getStore()).dump(S57.OC_BUAARE);

		// this won't be right after the info bar is done, but that will be resized later
		chart.setViewPortScreenArea(new Rectangle2D(0, 0, width, height));

		var mapSurface = makeMap();

		var toggle = new ToggleModel();
		setupPreferences(toggle);

		var infoBar = new InfoBar(platform, toggle, chart, unitProfile, displayOptions);

		var vbox = new VBox();
		vbox.getStyleClass().add("charts");
		VBox.setVgrow(mapSurface, Priority.ALWAYS);
		VBox.setVgrow(infoBar, Priority.NEVER);
		vbox.setFillWidth(true);
		vbox.getChildren().addAll(mapSurface, infoBar);

		vbox.setPickOnBounds(false);
		overlay.setPickOnBounds(false);

		var scene = new Scene(new StackPane(vbox, overlay), width, height);
		scene.getStylesheets().add(getClass().getResource("charts.css").toExternalForm());

		primaryStage.setScene(scene);
		platform.setTitle(primaryStage, "Knowtiphy Charts");
		primaryStage.sizeToScene();
		platform.setWindowIcons(primaryStage, getClass());
		// if (platform.isDesktop()) {
		// primaryStage.getIcons().addAll(new
		// Image(getClass().getResourceAsStream("knowtiphy_charts_icon_32.png")),
		// new Image(getClass().getResourceAsStream("knowtiphy_charts_icon_64.png")));
		// }
		primaryStage.show();
	}

	private ChartView makeMap() {
		return resizeable(new ChartView(chartLocker, chart, dynamics, unitProfile, displayOptions));
	}

	private void setupPreferences(ToggleModel toggle) {
		var displayProperties = FXUtils.nonResizeable(new PropertySheet(displayOptions.getProperties()));
		BorderPane.setAlignment(displayProperties, Pos.CENTER);
		displayProperties.setOnMouseExited(evt -> toggle.toggle());
		toggle.getStateProperty()
				.addListener(cl -> later(() -> overlay.setRight(toggle.isOn() ? displayProperties : null)));
	}

	private void showInitialSetup(IPlatform platform) {

		System.err.println("Platform = " + platform.getClass().getCanonicalName());
		System.err.println("File System root = " + platform.rootDir());
		System.err.println("File System root = " + platform.rootDir().toFile().exists());
		System.err.println("File System root ENC = " + Paths.get(platform.rootDir().toString(), "ENC"));
		System.err
				.println("File System root ENC = " + Paths.get(platform.rootDir().toString(), "ENC").toFile().exists());
		System.err.println("Charts  dir = " + platform.chartsDir());
		System.err.println("Charts  dir = " + platform.chartsDir().toFile().exists());
		try (var dave = Files.list(Paths.get(platform.rootDir().toString(), "ENC"))) {
			var files = dave.map(Path::getFileName).map(Path::toString).toList();
			System.err.println("Files = " + files);
		}
		catch (IOException ex) {
			Logger.getLogger(KnowtiphyCharts.class.getName()).log(Level.SEVERE, null, ex);
		}
		System.err.println("Screen dimensions = " + platform.screenDimensions());
		System.err.println("Screen ppi = " + platform.ppi());
		System.err.println("Screen ppcm = " + platform.ppcm());
		System.err.println("Default font = " + Font.getDefault());
		System.err.println("GPS Position = " + platform.positionProperty());
		platform.info();
		System.err.println();
	}

	public static void main(String[] args) {
		Application.launch(KnowtiphyCharts.class, args);
	}

}

// Setup.setup("/Users/graham/Documents/Charts/ENC/US_REGION08/08Region_ENCProdCat.xml");

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

// var xxx = getClass().getResourceAsStream("styles/BOYSPP.sld");
// System.err.println("XXX = " + xxx);
// var foo = xxx.readNBytes(5);
// System.err.println("Foo = " + Arrays.toString(foo));
// var yyy =
// getClass().getResourceAsStream("/org/knowtiphy/charts/styles//BOYSPP.sld");
// System.err.println("YYY = " + yyy);
// var bar = yyy.readNBytes(5);
// System.err.println("Foo = " + Arrays.toString(bar));

// Set categories = Collections.singleton(FunctionFactory.class);
// FactoryRegistry registry = new FactoryRegistry(categories);
// registry.registerFactory(new CRSProvider());
// ReferencingFactoryFinder.getCRSFactory(new Hints());
// ReferencingObjectFactory blah = new ReferencingObjectFactory();
