package org.knowtiphy.charts.chartview;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.chartlocker.ChartLocker;
import org.knowtiphy.charts.chartview.shapemapview.SingleCanvasShapeMapView;
import org.knowtiphy.charts.dynamics.AISModel;
import org.knowtiphy.charts.enc.Constants;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.ontology.S57;
import org.knowtiphy.charts.settings.UnitProfile;
import org.knowtiphy.charts.utils.FXUtils;
import static org.knowtiphy.charts.utils.FXUtils.resizeable;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.locationtech.jts.geom.Coordinate;

public class ChartViewSkin extends SkinBase<ChartView> implements Skin<ChartView> {

    private static final double PREFERRED_WIDTH = Region.USE_COMPUTED_SIZE;

    private static final double PREFERRED_HEIGHT = Region.USE_COMPUTED_SIZE;

    private final ChartView control;

    private final ChartLocker chartLocker;

    private final ChartViewModel chart;

    private final MapDisplayOptions displayOptions;

    private final UnitProfile unitProfile;

    private SingleCanvasShapeMapView<SimpleFeatureType, MemFeature> mapView;

    private Pane coordinateGrid;

    public ChartViewSkin(
            ChartView chartView,
            ChartLocker chartLocker,
            ChartViewModel chart,
            AISModel dynamics,
            UnitProfile unitProfile,
            MapDisplayOptions displayOptions) {

        super(chartView);

        this.control = chartView;
        this.chartLocker = chartLocker;
        this.chart = chart;
        this.unitProfile = unitProfile;
        this.displayOptions = displayOptions;
        initGraphics();
        registerListeners();
    }

    private void initGraphics() {

        if (Double.compare(control.getPrefWidth(), 0.0) <= 0
                || Double.compare(control.getPrefHeight(), 0.0) <= 0
                || Double.compare(control.getWidth(), 0.0) <= 0
                || Double.compare(control.getHeight(), 0.0) <= 0) {
            if (control.getPrefWidth() > 0 && control.getPrefHeight() > 0) {
                control.setPrefSize(control.getPrefWidth(), control.getPrefHeight());
            } else {
                control.setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        var root = createRoot();

        mapView = new SingleCanvasShapeMapView<>(chart, Color.LIGHTGREY);

        var quiltingOverlayView = createQuiltingOverlayView();
        coordinateGrid = createCoordinateGrid(unitProfile);

        root.getChildren().addAll(mapView, quiltingOverlayView, coordinateGrid); // ,
        getChildren().addAll(root);
    }

    private void registerListeners() {

        //  chart zoom drag, panning and re-positioning support
        DragPanZoomSupport.addZoomSupport(mapView, chart);
        DragPanZoomSupport.addDragSupport(mapView, chart);
        DragPanZoomSupport.addPanningSupport(mapView, chart);
        DragPanZoomSupport.addPositionAtSupport(mapView, chart);

        //  map context menu
        FXUtils.addContextMenuHandler(
                mapView,
                event ->
                        createContextMenu(event)
                                .show(mapView, event.getScreenX(), event.getScreenY()));

        unitProfile.unitChangeEvents().subscribe(_ -> mapView.requestLayout());
        chart.layerVisibilityEvent().subscribe(_ -> mapView.requestLayout());

        displayOptions.showGridEvents.subscribe(c -> coordinateGrid.setVisible(c.getNewValue()));

        displayOptions.showLightsEvents.subscribe(
                change -> chart.setLayerVisible(S57.OC_LIGHTS, change));
        displayOptions.showPlatformEvents.subscribe(
                change -> chart.setLayerVisible(S57.OC_OFSPLF, change));
        displayOptions.showWreckEvents.subscribe(
                change -> chart.setLayerVisible(S57.OC_WRECKS, change));
        displayOptions.showSoundingsEvents.subscribe(
                change -> chart.setLayerVisible(S57.OC_SOUNDG, change));
    }

    private StackPane createRoot() {

        return new StackPane() {

            @Override
            public void layoutChildren() {
                //                System.out.println("Skin layout children : " + getWidth() + " : "
                // + getHeight());
                // TODO -- is this sane? set the screen area of the viewport before laying out the
                // children
                chart.setViewPortScreenArea(new Rectangle2D(0, 0, getWidth(), getHeight()));
                super.layoutChildren();
            }
        };
    }

    private Pane createCoordinateGrid(UnitProfile unitProfile) {
        var grid = resizeable(new CoordinateGrid(chartLocker, chart, unitProfile));
        grid.setPickOnBounds(false);
        grid.setMouseTransparent(true);
        return grid;
    }

    private Pane createQuiltingOverlayView() {
        var overlayView = new QuiltOverlayView(chart);
        overlayView.setPickOnBounds(false);
        return overlayView;
    }

    private void showInfo(MouseEvent event) {

        //        try {
        //            // TODO are these the right x and y?
        //            var nearby = viewModel.featuresNearXYWorld(event.getX(), event.getY(), 1);
        //
        //            var tx = new Transformation(viewModel.viewPortScreenToWorld());
        //            tx.apply(event.getX(), event.getY());
        //
        //            // this is a bit weird since surely you can do it one query?
        //            var textToDisplay = new StringBuilder();
        //            textToDisplay.append(tx.getX()).append(", ").append(tx.getY()).append("\n");
        //
        //            for (var iterator : nearby) {
        //                while (iterator.hasNext()) {
        //                    var feature = iterator.next();
        //                    textToDisplay.append(feature.getIdentifier()).append("\n");
        //                    //
        // textToDisplay.append(feature.getDefaultGeometry()).append("\n");
        //                    for (var attr : feature.getFeatureType().getAttributeDescriptors()) {
        //                        if (!attr.getLocalName().equals("the_geom")) {
        //                            var attrVal = feature.getAttribute(attr.getLocalName());
        //                            if (attrVal != null && !(attrVal instanceof String x &&
        // x.isEmpty())) {
        //                                textToDisplay
        //                                        .append("\t")
        //                                        .append(attr.getName())
        //                                        .append(" = ")
        //                                        .append(attrVal)
        //                                        .append("\n");
        //                            }
        //                        }
        //                    }
        //                }
        //            }
        //
        //            var text = new TextArea(textToDisplay.toString());
        //
        //            var popOver = new PopOver(text);
        //            popOver.show(mapSurface, event.getScreenX(), event.getScreenY());
        //        } catch (Exception ex) {
        //            Logger.getLogger(ChartViewSkin.class.getName()).log(Level.SEVERE, null, ex);
        //        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void showMaxDetail(MouseEvent event) {

        ReferencedEnvelope envelope;
        try {
            var tx = new Transformation(chart.viewPortScreenToWorld());
            tx.apply(event.getX(), event.getY());
            chart.loadMostDetailedChart(
                    Constants.GEOMETRY_FACTORY.createPoint(new Coordinate(tx.getX(), tx.getY())));
        } catch (TransformException | NonInvertibleTransformException ex) {
            ex.printStackTrace();
            return;
        }
        //
        //        ENCCell mostDetailed = null;
        //        var smallestScale = Integer.MAX_VALUE;
        //
        //        for (var cell : chartLocker.intersections(envelope)) {
        //            if (cell.cScale() < smallestScale) {
        //                smallestScale = cell.cScale();
        //                mostDetailed = cell;
        //            }
        //        }
        //
        //        if (mostDetailed != null) {

    }

    private ContextMenu createContextMenu(MouseEvent mouseEvent) {

        var contextMenu = new ContextMenu();

        var maxDetail = new MenuItem("Max Detail Here");
        maxDetail.setOnAction(_ -> showMaxDetail(mouseEvent));

        var whatsHere = new MenuItem("What's here");
        whatsHere.setOnAction(_ -> showInfo(mouseEvent));

        contextMenu.getItems().addAll(maxDetail, whatsHere);
        return contextMenu;
    }
}

//  old stuff

    // private final Pane aisPane;
    // private final AISModel dynamics;

    // boat glyphs
//    private final Map<Long, Pair<AISInformation, Glyph>> boats = new HashMap<>();

        //        subscriptions.add(chartLocker.chartEvents().subscribe(change -> updateBoats()));
        // subscriptions.add(dynamics.aisEvents.subscribe(this::updateAISInformation));

//
//    private void updateAISInformation(AISEvent event) {
//        var asInfo = event.aisInformation();
//        var id = asInfo.getId();
//        if (!boats.containsKey(id)) {
//            var newBoat = Fonts.boat();
//            boats.put(id, Pair.of(asInfo, newBoat));
//            setBoatPosition(newBoat, asInfo);
//            // later(() -> aisPane.getChildren().add(newBoat));
//        } else {
//            var boat = boats.get(id).getRight();
//            boats.put(id, Pair.of(asInfo, boat));
//            setBoatPosition(boat, asInfo);
//        }
//    }
//
//    @SuppressWarnings("CallToPrintStackTrace")
//    private void setBoatPosition(Glyph boat, AISInformation aisInfo) {
//        // need to clip the position?
//        Transformation tx;
//        try {
//            tx = new Transformation(chart.viewPortWorldToScreen());
//        } catch (TransformException | NonInvertibleTransformException ex) {
//            ex.printStackTrace();
//            return;
//        }
//        tx.apply(aisInfo.getPosition().x, aisInfo.getPosition().y);
//        boat.setTranslateX(tx.getX());
//        boat.setTranslateY(tx.getY());
//    }
//
//    private void updateBoats() {
//        for (var boat : boats.values()) {
//            setBoatPosition(boat.getRight(), boat.getLeft());
//        }
//    }
//
//    private Pane makeDynamicsSurface() {
//        var pane = new Pane();
//        pane.setPickOnBounds(false);
//        pane.widthProperty().addListener(cl -> updateBoats());
//        pane.heightProperty().addListener(cl -> updateBoats());
//        return pane;
//    }
//
//    private Pane makeIconsSurface() {
//        return new IconSurface(chart);
//    }

        // subscriptions.add(chart.viewPortBoundsEvent.subscribe(change ->
        // updateBoats()));

        // this.dynamics = dynamics;
    // private final Pane iconsSurface;
        //        var surfaceDragEventsPane = new Pane();
        // iconsSurface = makeIconsSurface();
             // aisPane = makeDynamicsSurface();