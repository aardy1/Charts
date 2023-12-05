package org.knowtiphy.charts.chartview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.NonInvertibleTransformException;
import org.apache.commons.lang3.tuple.Pair;
import org.controlsfx.glyphfont.Glyph;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.Fonts;
import org.knowtiphy.charts.UnitProfile;
import org.knowtiphy.charts.chartview.ChartView.EventModel;
import org.knowtiphy.charts.dynamics.AISEvent;
import org.knowtiphy.charts.dynamics.AISInformation;
import org.knowtiphy.charts.dynamics.AISModel;
import org.knowtiphy.charts.enc.ChartDescription;
import org.knowtiphy.charts.enc.ChartLocker;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.charts.geotools.Queries;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.view.ShapeMapView;
import org.reactfx.Change;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;

public class ChartViewSkin extends SkinBase<ChartView> implements Skin<ChartView> {

	private static final double PREFERRED_WIDTH = Region.USE_COMPUTED_SIZE;

	private static final double PREFERRED_HEIGHT = Region.USE_COMPUTED_SIZE;

	private final StackPane root;

	private final ShapeMapView mapSurface;

	private final ChartLocker chartLocker;

	private ENCChart chart;

	// private final AISModel dynamics;

	private final EventModel eventModel;

	private final MapDisplayOptions displayOptions;

	private final List<Subscription> subscriptions = new ArrayList<>();

	// private final Pane iconsSurface;

	// private final Pane quiltingSurface;

	private final Pane coordinateGrid;

	// private final Pane aisPane;

	// boat glyphs
	private final Map<Long, Pair<AISInformation, Glyph>> boats = new HashMap<>();

	public ChartViewSkin(ChartView fxMap, ChartLocker chartLocker, ENCChart chrt, AISModel dynamics,
			EventModel eventModel, UnitProfile unitProfile, MapDisplayOptions displayOptions) {

		super(fxMap);

		this.chartLocker = chartLocker;
		this.chart = chrt;
		// this.dynamics = dynamics;
		this.eventModel = eventModel;
		this.displayOptions = displayOptions;

		root = makeRoot();
		getChildren().addAll(root);

		var surfaceDragEventsPane = new Pane();
		mapSurface = makeMapSurface();
		// iconsSurface = makeIconsSurface();
		// quiltingSurface = makeQuiltingSurface();
		coordinateGrid = makeCoordinateGrid(unitProfile);
		// aisPane = makeDynamicsSurface();

		root.getChildren().addAll(surfaceDragEventsPane, mapSurface, coordinateGrid);// ,
		// iconsSurface,
		// //
		// quiltingSurface,
		// aisPane);

		if (Double.compare(S().getPrefWidth(), 0.0) <= 0 || Double.compare(S().getPrefHeight(), 0.0) <= 0
				|| Double.compare(S().getWidth(), 0.0) <= 0 || Double.compare(S().getHeight(), 0.0) <= 0) {
			if (S().getPrefWidth() > 0 && S().getPrefHeight() > 0) {
				S().setPrefSize(S().getPrefWidth(), S().getPrefHeight());
			}
			else {
				S().setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
			}
		}

		eventModel.mouseEvents.feedFrom(EventStreams.eventsOf(root, MouseEvent.ANY));
		eventModel.scrollEvents.feedFrom(EventStreams.eventsOf(root, ScrollEvent.ANY));
		eventModel.zoomEvents.feedFrom(EventStreams.eventsOf(root, ZoomEvent.ANY));

		// windows on clicked, mac on pressed
		eventModel.mouseClicked.filter(event -> event.isPopupTrigger())
				.subscribe(event -> makeContextMenu(event).show(mapSurface, event.getScreenX(), event.getScreenY()));
		eventModel.mousePressed.filter(event -> event.isPopupTrigger())
				.subscribe(event -> makeContextMenu(event).show(mapSurface, event.getScreenX(), event.getScreenY()));

		setupListeners();
	}

	private StackPane makeRoot() {
		return new StackPane() {

			@Override
			public void layoutChildren() {
				try {
					// set the screen area for all the children before laying them out
					chart.setViewPortScreenArea(new Rectangle2D(0, 0, (int) getWidth(), (int) getHeight()));
				}
				catch (TransformException | NonInvertibleTransformException ex) {
					Logger.getLogger(ChartViewSkin.class.getName()).log(Level.SEVERE, null, ex);
				}

				super.layoutChildren();
			}
		};
	}

	private Pane makeCoordinateGrid(UnitProfile unitProfile) {

		var theGrid = new CoordinateGrid(chart, unitProfile);
		theGrid.setPickOnBounds(false);
		theGrid.setMouseTransparent(true);
		return theGrid;
	}

	private Pane makeIconsSurface() {
		return new IconSurface(chart);
	}

	private Pane makeQuiltingSurface() {
		var theSurface = new QuiltingSurface(chartLocker, chart, displayOptions);
		theSurface.setPickOnBounds(false);
		return theSurface;
	}

	private Pane makeDynamicsSurface() {
		var pane = new Pane();
		pane.setPickOnBounds(false);
		pane.widthProperty().addListener(cl -> updateBoats());
		pane.heightProperty().addListener(cl -> updateBoats());
		return pane;
	}

	private ShapeMapView makeMapSurface() {
		var theSurface = new ShapeMapView(chart);
		theSurface.setMouseTransparent(true);
		return theSurface;
	}

	private void setupListeners() {

		// unsubscribe listeners on the old chart
		subscriptions.forEach(s -> s.unsubscribe());
		subscriptions.clear();

		// add listeners on the new chart
		subscriptions.add(DragPanZoomSupport.addPositionAtSupport(eventModel, chart));
		subscriptions.add(DragPanZoomSupport.addDragSupport(eventModel, chart));
		subscriptions.add(DragPanZoomSupport.addPanningSupport(eventModel, chart));
		subscriptions.add(DragPanZoomSupport.addZoomSupport(eventModel, chart));

		// subscriptions.add(chart.displayOptions().showGridEvents.subscribe(c ->
		// gridPane.setVisible(c.getNewValue())));

		// subscriptions.add(chart.viewPortBoundsEvent.subscribe(change ->
		// updateBoats()));
		subscriptions.add(chart.newMapEvent().subscribe(change -> updateBoats()));
		// subscriptions.add(dynamics.aisEvents.subscribe(this::updateAISInformation));

		subscriptions.add(chart.newMapEvent().subscribe(change -> {
			chart = (ENCChart) change.getNewValue();
			setupListeners();
		}));
	}

	private void showInfo(MouseEvent event) {

		// try {
		// // TODO are these the right x and y?
		// List<IFeatureSourceIterator<SimpleFeatureType, IFeature>> nearby = Queries
		// .<SimpleFeatureType, IFeature>featuresNearXYWorld(chart, event.getX(),
		// event.getY(), 1);
		//
		// var tx = new Transformation(chart.viewPortScreenToWorld());
		// tx.apply(event.getX(), event.getY());
		//
		// // this is a bit weird since surely you can do it one query?
		// var textToDisplay = new StringBuilder();
		// textToDisplay.append(tx.getX()).append(", ").append(tx.getY()).append("\n");
		//
		// for (var iterator : nearby) {
		// while (iterator.hasNext()) {
		// var feature = iterator.next();
		// textToDisplay.append(feature.getIdentifier()).append("\n");
		// textToDisplay.append(feature.getDefaultGeometry()).append("\n");
		// for (var attr : feature.getFeatureType().getAttributeDescriptors()) {
		// if (!attr.getLocalName().equals("the_geom")) {
		// var attrVal = feature.getAttribute(attr.getLocalName());
		// if (attrVal != null && !(attrVal instanceof String x && x.isEmpty())) {
		// textToDisplay.append("\t").append(attr.getName()).append(" = ").append(attrVal)
		// .append("\n");
		// }
		// }
		// }
		// }
		// }
		//
		// var text = new TextArea(textToDisplay.toString());
		//
		// var popOver = new PopOver(text);
		// popOver.show(mapSurface, event.getScreenX(), event.getScreenY());
		// }
		// catch (IOException ex) {
		// Logger.getLogger(ChartViewSkin.class.getName()).log(Level.SEVERE, null, ex);
		// }
	}

	private void showMaxDetail(MouseEvent event) {

		var envelope = Queries.tinyPolygon(chart, event.getX(), event.getY());

		ChartDescription mostDetailedChart = null;
		var smallestScale = Integer.MAX_VALUE;

		for (var chartDescription : chartLocker.intersections(envelope)) {
			if (chartDescription.cScale() < smallestScale) {
				smallestScale = chartDescription.cScale();
				mostDetailedChart = chartDescription;
			}
		}

		if (mostDetailedChart != null) {
			var screenArea = new Rectangle2D(0, 0, (int) root.getWidth(), (int) root.getHeight());
			try {
				var newChart = chartLocker.loadChart(mostDetailedChart, displayOptions, screenArea);
				newChart.newMapEvent().push(new Change<>(chart, newChart));
			}
			catch (TransformException | FactoryException | NonInvertibleTransformException | StyleSyntaxException ex) {
				Logger.getLogger(ChartViewSkin.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private ContextMenu makeContextMenu(MouseEvent mouseEvent) {
		var contextMenu = new ContextMenu();
		var maxDetail = new MenuItem("Max Detail Here");
		var whatsHere = new MenuItem("What's here");

		maxDetail.setOnAction((ActionEvent event) -> showMaxDetail(mouseEvent));
		whatsHere.setOnAction((ActionEvent event) -> showInfo(mouseEvent));

		contextMenu.getItems().addAll(maxDetail, whatsHere);
		return contextMenu;
	}

	private ChartView S() {
		return getSkinnable();
	}

	private void updateAISInformation(AISEvent event) {
		var asInfo = event.getAisInformation();
		var id = asInfo.getId();
		if (!boats.containsKey(id)) {
			var newBoat = Fonts.boat();
			boats.put(id, Pair.of(asInfo, newBoat));
			setBoatPosition(newBoat, asInfo);
			// later(() -> aisPane.getChildren().add(newBoat));
		}
		else {
			var boat = boats.get(id).getRight();
			boats.put(id, Pair.of(asInfo, boat));
			setBoatPosition(boat, asInfo);
		}
	}

	private void setBoatPosition(Glyph boat, AISInformation aisInfo) {
		var tx = new Transformation(chart.viewPortWorldToScreen());
		tx.apply(aisInfo.getPosition().x, aisInfo.getPosition().y);
		boat.setTranslateX(tx.getX());
		boat.setTranslateY(tx.getY());
	}

	private void updateBoats() {
		for (var boat : boats.values()) {
			setBoatPosition(boat.getRight(), boat.getLeft());
		}
	}

}