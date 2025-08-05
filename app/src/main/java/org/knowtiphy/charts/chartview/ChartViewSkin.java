package org.knowtiphy.charts.chartview;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
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
import org.apache.commons.lang3.tuple.Pair;
import org.controlsfx.glyphfont.Glyph;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.Fonts;
import org.knowtiphy.charts.chartlocker.ChartLocker;
import org.knowtiphy.charts.chartview.shapemapview.SingleCanvasShapeMapView;
import org.knowtiphy.charts.dynamics.AISEvent;
import org.knowtiphy.charts.dynamics.AISInformation;
import org.knowtiphy.charts.dynamics.AISModel;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.ontology.S57;
import org.knowtiphy.charts.settings.UnitProfile;
import org.knowtiphy.charts.utils.DragState;
import org.knowtiphy.charts.utils.FXUtils;
import static org.knowtiphy.charts.utils.FXUtils.resizeable;
import org.knowtiphy.shapemap.context.SVGCache;
import org.knowtiphy.shapemap.renderer.Transformation;

public class ChartViewSkin extends SkinBase<ChartView> implements Skin<ChartView> {

    private static final double PREFERRED_WIDTH = Region.USE_COMPUTED_SIZE;

    private static final double PREFERRED_HEIGHT = Region.USE_COMPUTED_SIZE;

    private final ChartLocker chartLocker;

    private ChartViewModel viewModel;

    // private final AISModel dynamics;

    private final MapDisplayOptions displayOptions;

    private final UnitProfile unitProfile;

    private final SVGCache svgCache;

    // private final Pane iconsSurface;
    private SingleCanvasShapeMapView<SimpleFeatureType, MemFeature> mapSurface;

    private Pane coordinateGrid;

    // private final Pane aisPane;

    // boat glyphs
    private final Map<Long, Pair<AISInformation, Glyph>> boats = new HashMap<>();

    public ChartViewSkin(
            ChartView chartView,
            ChartLocker chartLocker,
            ChartViewModel viewModel,
            AISModel dynamics,
            UnitProfile unitProfile,
            MapDisplayOptions displayOptions,
            SVGCache svgCache) {
        super(chartView);

        this.chartLocker = chartLocker;
        this.viewModel = viewModel;
        this.unitProfile = unitProfile;
        // this.dynamics = dynamics;
        this.displayOptions = displayOptions;
        this.svgCache = svgCache;
        //        eventModel.mouseEvents.feedFrom(EventStreams.eventsOf(root,
        // MouseEvent.ANY));
        //        eventModel.scrollEvents.feedFrom(EventStreams.eventsOf(root, ScrollEvent.ANY));
        //        eventModel.zoomEvents.feedFrom(EventStreams.eventsOf(root, ZoomEvent.ANY));

        //        subscriptions.add(chartLocker.chartEvents().subscribe(change -> updateBoats()));
        // subscriptions.add(dynamics.aisEvents.subscribe(this::updateAISInformation));

        initGraphics();
        registerListeners();
    }

    private void initGraphics() {

        if (Double.compare(S().getPrefWidth(), 0.0) <= 0
                || Double.compare(S().getPrefHeight(), 0.0) <= 0
                || Double.compare(S().getWidth(), 0.0) <= 0
                || Double.compare(S().getHeight(), 0.0) <= 0) {
            if (S().getPrefWidth() > 0 && S().getPrefHeight() > 0) {
                S().setPrefSize(S().getPrefWidth(), S().getPrefHeight());
            } else {
                S().setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        var root = makeRoot();

        mapSurface =
                new SingleCanvasShapeMapView<SimpleFeatureType, MemFeature>(
                        viewModel, Color.LIGHTGREY);

        var surfaceDragEventsPane = new Pane();
        // iconsSurface = makeIconsSurface();
        var quiltingSurface = makeQuiltingSurface();
        coordinateGrid = coordinateGrid(unitProfile);
        // aisPane = makeDynamicsSurface();

        root.getChildren()
                .addAll(surfaceDragEventsPane, mapSurface, quiltingSurface, coordinateGrid); // ,
        // iconsSurface,
        // //
        // aisPane);

        getChildren().addAll(root);
    }

    private void registerListeners() {

        //  chart  re-positioning
        FXUtils.addDoubleClickHandler(
                mapSurface,
                event -> {
                    try {
                        viewModel.positionAt(event.getX(), event.getY());
                    } catch (TransformException | NonInvertibleTransformException ex) {
                        ex.printStackTrace();
                    }
                });

        //  chart zooming
        FXUtils.addZoomHandler(
                mapSurface,
                event -> {
                    // not sure what NaN means -- something to do with Zoom start/finish
                    if (!Double.isNaN(event.getZoomFactor())) {
                        try {
                            viewModel.setZoom(viewModel.zoom() * event.getZoomFactor());
                        } catch (TransformException | NonInvertibleTransformException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

        //  chart dragging
        FXUtils.addDragHandler(
                mapSurface,
                (event, dragState) -> {
                    var difX = event.getX() - dragState.startX;
                    var difY = event.getY() - dragState.startY;
                    dragState.startX = event.getX();
                    dragState.startY = event.getY();
                    var newPos = new Point2D(difX, difY);
                    var result = viewModel.viewPortScreenToWorld().transform(newPos);

                    var newVPBounds = new ReferencedEnvelope(viewModel.viewPortBounds());
                    newVPBounds.translate(
                            newVPBounds.getMinimum(0) - result.getX(),
                            newVPBounds.getMaximum(1) - result.getY());

                    try {
                        viewModel.setViewPortBounds(newVPBounds);
                    } catch (TransformException | NonInvertibleTransformException ex) {
                        ex.printStackTrace();
                    }
                });

        FXUtils.addContextMenuHandler(
                mapSurface,
                event ->
                        makeContextMenu(event)
                                .show(mapSurface, event.getScreenX(), event.getScreenY()));

        //  listeners that don't depend on the chart
        unitProfile.unitChangeEvents().subscribe(_ -> mapSurface.requestLayout());

        //        subscriptions.addAll(DragPanZoomSupport.addPanningSupport(eventModel, viewModel));

        displayOptions.showGridEvents.subscribe(c -> coordinateGrid.setVisible(c.getNewValue()));

        for (var map : viewModel.maps()) {
            displayOptions.showLightsEvents.subscribe(
                    change -> map.layer(S57.OC_LIGHTS).setVisible(change.getNewValue()));
            displayOptions.showPlatformEvents.subscribe(
                    change -> map.layer(S57.OC_OFSPLF).setVisible(change.getNewValue()));
            displayOptions.showWreckEvents.subscribe(
                    change -> map.layer(S57.OC_WRECKS).setVisible(change.getNewValue()));
            displayOptions.showSoundingsEvents.subscribe(
                    change -> map.layer(S57.OC_SOUNDG).setVisible(change.getNewValue()));
        }

        viewModel.layerVisibilityEvent().subscribe(b -> mapSurface.requestLayout());

        // subscriptions.add(chart.viewPortBoundsEvent.subscribe(change ->
        // updateBoats()));
    }

    private StackPane makeRoot() {
        return new StackPane() {

            @Override
            public void layoutChildren() {
                System.out.println(
                        "Skin layout children : " + (int) getWidth() + " : " + (int) getHeight());
                try {
                    // set the screen area of the viewport before laying out the children
                    viewModel.setViewPortScreenArea(
                            new Rectangle2D(0, 0, (int) getWidth(), (int) getHeight()));
                } catch (TransformException | NonInvertibleTransformException ex) {
                    Logger.getLogger(ChartViewSkin.class.getName()).log(Level.SEVERE, null, ex);
                }

                super.layoutChildren();
            }
        };
    }

    private Pane coordinateGrid(UnitProfile unitProfile) {
        var grid = resizeable(new CoordinateGrid(chartLocker, viewModel, unitProfile));
        grid.setPickOnBounds(false);
        grid.setMouseTransparent(true);
        return grid;
    }

    private Pane makeQuiltingSurface() {
        var theSurface = new QuiltingSurface(viewModel);
        theSurface.setPickOnBounds(false);
        return theSurface;
    }

    /**
     * Create a single canvas shape map view and add dragging, zooming etc support
     *
     * @return the shape map view
     */
    private Pane makeDynamicsSurface() {
        var pane = new Pane();
        pane.setPickOnBounds(false);
        pane.widthProperty().addListener(cl -> updateBoats());
        pane.heightProperty().addListener(cl -> updateBoats());
        return pane;
    }

    private Pane makeIconsSurface() {
        return new IconSurface(viewModel);
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

    private void showMaxDetail(MouseEvent event) {
        var envelope = viewModel.tinyPolygon(event.getX(), event.getY());

        ENCCell mostDetailed = null;
        var smallestScale = Integer.MAX_VALUE;

        for (var cell : chartLocker.intersections(envelope)) {
            if (cell.cScale() < smallestScale) {
                smallestScale = cell.cScale();
                mostDetailed = cell;
            }
        }

        //    if(mostDetailed != null && mostDetailed != chart.cell())
        //    {
        //      try
        //      {
        //        chartLocker.loadChart(mostDetailed, displayOptions, svgCache);
        //      }
        //      catch(TransformException | FactoryException | NonInvertibleTransformException |
        //            StyleSyntaxException ex)
        //      {
        //        Logger.getLogger(ChartViewSkin.class.getName()).log(Level.SEVERE, null, ex);
        //      }
        //    }
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
        var asInfo = event.aisInformation();
        var id = asInfo.getId();
        if (!boats.containsKey(id)) {
            var newBoat = Fonts.boat();
            boats.put(id, Pair.of(asInfo, newBoat));
            setBoatPosition(newBoat, asInfo);
            // later(() -> aisPane.getChildren().add(newBoat));
        } else {
            var boat = boats.get(id).getRight();
            boats.put(id, Pair.of(asInfo, boat));
            setBoatPosition(boat, asInfo);
        }
    }

    private void setBoatPosition(Glyph boat, AISInformation aisInfo) {
        // need to clip the position?
        var tx = new Transformation(viewModel.viewPortWorldToScreen());
        tx.apply(aisInfo.getPosition().x, aisInfo.getPosition().y);
        boat.setTranslateX(tx.getX());
        boat.setTranslateY(tx.getY());
    }

    private void updateBoats() {
        for (var boat : boats.values()) {
            setBoatPosition(boat.getRight(), boat.getLeft());
        }
    }

    private void doDrag(MouseEvent event, DragState dragState) {
        var difX = event.getX() - dragState.startX;
        var difY = event.getY() - dragState.startY;
        dragState.startX = event.getX();
        dragState.startY = event.getY();
        var newPos = new Point2D(difX, difY);
        var result = viewModel.viewPortScreenToWorld().transform(newPos);

        var newVPBounds = new ReferencedEnvelope(viewModel.viewPortBounds());
        newVPBounds.translate(
                newVPBounds.getMinimum(0) - result.getX(),
                newVPBounds.getMaximum(1) - result.getY());

        try {
            viewModel.setViewPortBounds(newVPBounds);
        } catch (TransformException | NonInvertibleTransformException ex) {
            Logger.getLogger(DragPanZoomSupport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}