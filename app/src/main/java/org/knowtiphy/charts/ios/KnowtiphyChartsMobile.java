/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.ios;

/**
 * @author graham
 */
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.mvc.View;
import java.beans.IntrospectionException;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;
import javax.xml.stream.XMLStreamException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.KnowtiphyCharts;
import org.knowtiphy.charts.UnitProfile;
import org.knowtiphy.charts.dynamics.AISModel;
import org.knowtiphy.charts.enc.CatalogReader;
import org.knowtiphy.charts.enc.ChartLocker;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.charts.enc.LocalChartProvider;
import org.knowtiphy.charts.ios.view.ChartViewView;
import org.knowtiphy.charts.memstore.MapStats;
import org.knowtiphy.charts.memstore.StyleReader;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.viewmodel.MapDisplayOptions;

import static com.gluonhq.charm.glisten.application.AppManager.HOME_VIEW;

public class KnowtiphyChartsMobile extends Application {

	private final AppManager appManager = AppManager.initialize(this::postInit);

	private final UnitProfile unitProfile = new UnitProfile();

	private ChartLocker chartLocker;

	private MapDisplayOptions displayOptions;

	private final AISModel dynamics = new AISModel();

	private ENCChart chart;

	private ChartViewView chartViewView;

	@Override
	public void init() throws TransformException, NonInvertibleTransformException, IntrospectionException, IOException,
			XMLStreamException, FactoryException, StyleSyntaxException {

		var platform = org.knowtiphy.charts.platform.Platform.getPlatform();

		var catalogFile = platform.chartsDir().resolve("08Region_ENCProdCat.xml");// 08Region_ENCProdCat.xml");
		var catalog = new CatalogReader(catalogFile).read();

		// var styleReader = new StyleReader(styleDir);
		var styleReader = new StyleReader(KnowtiphyCharts.class);
		var chartProvider = new LocalChartProvider(catalog, platform.chartsDir(), styleReader);
		chartLocker = new ChartLocker(chartProvider);
		var chartDescription = chartProvider.getChartDescription("Gulf of Mexico", 2_160_000);

		displayOptions = new MapDisplayOptions();
		chart = chartLocker.getChart(chartDescription, displayOptions);
		var stats = new MapStats(chart).stats();
		stats.print();

		appManager.addViewFactory(HOME_VIEW, () -> {
			return new View(new VBox(new Label("Dave")));
			// try {
			// chartViewView = new ChartViewView(unitProfile, chartLocker, displayOptions,
			// dynamics, chart);
			// }
			// catch (NonInvertibleTransformException | TransformException ex) {
			// Logger.getLogger(KnowtiphyChartsMobile.class.getName()).log(Level.SEVERE,
			// null, ex);
			// }
			// return chartViewView;
		});

		// appManager.addViewFactory(Names.MY_BOAT_VIEW, () -> new MyBoatView(platform));
		// appManager.addViewFactory(Names.CHART_LOCKER_VIEW, ChartLockerView::new);
	}

	public void postInit(Scene scene) {
		// Swatch.TEAL.assignTo(scene);
		//
		// if (Platform.isDesktop()) {
		// // ((Stage) scene.getWindow()).getIcons().add(new
		// // Image(GluonRubik.class.getResourceAsStream("/icon.png")));
		// Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
		// scene.getWindow().setWidth(visualBounds.getWidth());
		// scene.getWindow().setHeight(visualBounds.getHeight());
		// }
	}

	@Override
	public void start(Stage stage) {
		appManager.start(stage);
	}

	public static void main(String[] args) {
		launch();
	}

}
