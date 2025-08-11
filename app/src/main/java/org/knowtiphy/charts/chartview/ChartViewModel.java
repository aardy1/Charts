/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.chartview;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.TextAdapter;
import org.knowtiphy.charts.chartlocker.ChartLocker;
import org.knowtiphy.charts.chartview.shapemapview.IShapeMapViewModel;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.map.Map;
import org.knowtiphy.charts.map.Quilt;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.platform.IUnderlyingPlatform;
import org.knowtiphy.charts.settings.AppSettings;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IFeatureSourceIterator;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.api.ITextAdapter;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.locationtech.jts.geom.Point;
import org.reactfx.Change;
import org.reactfx.EventSource;
import org.reactfx.EventStream;

/**
 * An ENC chart view model -- a quilt of ENC cells (loaded from a chart locker) together with a
 * viewport, event streams, etc.
 */
public class ChartViewModel implements IShapeMapViewModel<MemFeature> {

    private static final double DEFAULT_WIDTH = 3;

    private static final double DEFAULT_HEIGHT = 3;

    //  on zoom in change the view port to be smaller by this factor
    private static final double ZOOM_FACTOR_MULTIPLIER = 0.7;

    //  the quilt can change to a whole new quilt object
    private Quilt<MemFeature> quilt;

    private final MapViewport viewPort;
    private final ChartLocker chartLocker;
    private final AppSettings appSettings;
    private final MapDisplayOptions mapDisplayOptions;
    private final IUnderlyingPlatform platform;

    private final IFeatureAdapter<MemFeature> featureAdapter;
    private final IRenderablePolygonProvider renderablePolygonProvider;
    private final ISVGProvider svgProvider;
    //    private final ITextBoundsFunction textBoundsFunction;

    private final EventSource<Change<Quilt<MemFeature>>> quiltChangeEvent;
    private final EventSource<Change<ReferencedEnvelope>> viewPortBoundsEvent;
    private final EventStream<Change<Rectangle2D>> viewPortScreenAreaEvent;
    private final EventSource<Change<Boolean>> layerVisibilityEvent;

    public ChartViewModel(
            Quilt<MemFeature> quilt,
            MapViewport viewPort,
            ChartLocker chartLocker,
            AppSettings appSettings,
            MapDisplayOptions mapDisplayOptions,
            IUnderlyingPlatform platform,
            IFeatureAdapter<MemFeature> featureAdapter,
            IRenderablePolygonProvider renderablePolygonProvider,
            ISVGProvider svgProvider
            //            ITextBoundsFunction textBoundsFunction
            ) {

        this.quilt = quilt;
        this.viewPort = viewPort;
        this.chartLocker = chartLocker;
        this.appSettings = appSettings;
        this.mapDisplayOptions = mapDisplayOptions;
        this.platform = platform;
        this.featureAdapter = featureAdapter;
        this.renderablePolygonProvider = renderablePolygonProvider;
        this.svgProvider = svgProvider;
        //        this.textBoundsFunction = textBoundsFunction;

        quiltChangeEvent = new EventSource<>();
        viewPortBoundsEvent = new EventSource<>();
        viewPortScreenAreaEvent = new EventSource<>();
        layerVisibilityEvent = new EventSource<>();
    }

    @Override
    public List<Map<MemFeature>> maps() {
        return quilt.maps();
    }

    //  TODO -- assumes that all maps have the same CRS -- is this too strong an assumption?
    @Override
    public CoordinateReferenceSystem crs() {
        return viewPortBounds().getCoordinateReferenceSystem();
    }

    @Override
    public ReferencedEnvelope viewPortBounds() {
        return viewPort.getBounds();
    }

    //  SCAMIN and cScale are based on some 22inch screen so adjust
    //  TODO -- this is basically just a guess -- and a total fudge
    @Override
    public double aScale() {
        var swIn = platform.screenDimensions().getWidth() / platform.ppi();
        var adjustment = swIn / 22;
        return dScale() * adjustment / 2;
    }

    @Override
    public Affine viewPortScreenToWorld()
            throws TransformException, NonInvertibleTransformException {
        return viewPort.getScreenToWorld();
    }

    @Override
    public Affine viewPortWorldToScreen()
            throws TransformException, NonInvertibleTransformException {
        return viewPort.getWorldToScreen();
    }

    //  do we need this -- surely the screen area is computed by the widgets?
    @Override
    public Rectangle2D viewPortScreenArea() {
        return viewPort.getScreenArea();
    }

    @Override
    public EventStream<Change<Quilt<MemFeature>>> quiltChangeEvent() {
        return quiltChangeEvent;
    }

    @Override
    public EventStream<Change<ReferencedEnvelope>> viewPortBoundsEvent() {
        return viewPortBoundsEvent;
    }

    @Override
    public EventStream<Change<Rectangle2D>> viewPortScreenAreaEvent() {
        return viewPortScreenAreaEvent;
    }

    @Override
    public EventStream<Change<Boolean>> layerVisibilityEvent() {
        return layerVisibilityEvent;
    }

    @Override
    public IFeatureAdapter<MemFeature> featureAdapter() {
        return featureAdapter;
    }

    @Override
    public IRenderablePolygonProvider renderablePolygonProvider() {
        return renderablePolygonProvider;
    }

    @Override
    public ISVGProvider svgProvider() {
        return svgProvider;
    }

    @Override
    public ITextAdapter textSizeProvider() {
        return new TextAdapter();
    }

    public AppSettings getAppSettings() {
        return appSettings;
    }

    public MapDisplayOptions getMapDisplayOptions() {
        return mapDisplayOptions;
    }

    public EventSource<Change<Boolean>> getLayerVisibilityEvent() {
        return layerVisibilityEvent;
    }

    @Override
    public List<IFeatureSourceIterator<MemFeature>> featuresNearXYWorld(
            double x, double y, int radius)
            throws TransformException, NonInvertibleTransformException { // throws Exception {

        var envelope = tinyPolygon(x, y, radius);

        var result = new ArrayList<IFeatureSourceIterator<MemFeature>>();

        for (var map : maps()) {
            for (var layer : map.layers()) {
                result.add(layer.featureSource().features(envelope, Double.MIN_VALUE));
            }
        }

        return result;
    }

    public double dScale() {
        return viewPort.dScale();
    }

    public void changeZoomByFactor(double zoomFactor) {
        viewPort.zoomBy(zoomFactor);
        recomputeQuilt();
    }

    public void incZoom() {
        changeZoomByFactor(ZOOM_FACTOR_MULTIPLIER);
    }

    public void decZoom() {
        changeZoomByFactor(1 / ZOOM_FACTOR_MULTIPLIER);
    }

    public boolean isQuilt() {
        return quilt.maps().size() > 1;
    }

    public int cScale() {
        return quilt.maps().get(0).cScale();
    }

    //  TODO
    public String title() {
        //  only makes sense if the chart is not a quilt
        return maps().get(0).title();
    } // return maps().get(0).lName();}

    public void setViewPortBounds(ReferencedEnvelope newBounds) {

        viewPort.setBounds(newBounds);
        //  only need to do this if the viewport bounds have actually changed
        recomputeQuilt();
    }

    public void setViewPortScreenArea(Rectangle2D screenArea) {
        viewPort.setScreenArea(screenArea);
    }

    public void setLayerVisible(String featureType, Change<Boolean> change) {

        for (var map : quilt.maps()) {
            var layer = map.layer(featureType);
            assert layer != null : featureType;
            layer.setVisible(change.getNewValue());
        }

        layerVisibilityEvent.push(change);
    }

    // need to make this adaptive --  like 1/2 the maxX
    public void positionAt(double x, double y)
            throws TransformException, NonInvertibleTransformException {

        var world = viewPortBounds();

        var defaultWidth =
                world.getWidth() <= 2 * DEFAULT_WIDTH ? world.getWidth() / 8 : DEFAULT_WIDTH;
        var defaultHeight =
                world.getHeight() <= 2 * DEFAULT_HEIGHT ? world.getHeight() / 8 : DEFAULT_HEIGHT;

        // TODO -- why does this not center the view port on the x, y world coords?
        var tx = new Transformation(viewPortScreenToWorld());
        tx.apply(x, y);

        var envelope =
                new ReferencedEnvelope(
                        tx.getX() - defaultWidth,
                        tx.getX() + defaultWidth,
                        tx.getY() - defaultHeight,
                        tx.getY() + defaultHeight,
                        crs());
        //    var newExtent = clip(map, envelope);
        setViewPortBounds(envelope);
    }

    public void loadNewChart(ENCCell cell) {
        System.out.println("load new chart");
        setViewPortBounds(cell.bounds());
        setQuilt(chartLocker.loadQuilt(cell.bounds(), aScale(), appSettings, mapDisplayOptions));
    }

    public void loadMostDetailedChart(Point point) {
        loadNewChart(chartLocker.getMostDetailedCell(point));
    }

    public ReferencedEnvelope tinyPolygon(double x, double y, int radius)
            throws TransformException, NonInvertibleTransformException {
        int screenMinX = (int) x - radius;
        int screenMinY = (int) y - radius;
        int screenMaxX = (int) x + radius;
        int screenMaxY = (int) y + radius;
        /*
         * Transform the screen rectangle into bounding box in the coordinate reference
         * system of our map context. Note: we are using a naive method here but GeoTools
         * also offers other, more accurate methods.
         */
        Transformation tx = new Transformation(viewPortScreenToWorld());
        tx.apply(screenMinX, screenMinY);
        double minX = tx.getX();
        double minY = tx.getY();
        tx.apply(screenMaxX, screenMaxY);
        double maxX = tx.getX();
        double maxY = tx.getY();
        double width = maxX - minX;
        double height = maxY - minY;
        // TODO -- fix this as upside down
        return new ReferencedEnvelope(minX, minX + width, minY, minY + height, crs());
    }

    private void setQuilt(Quilt<MemFeature> newQuilt) {
        //  TODO this is a hack :-)
        if (newQuilt.maps().isEmpty()) return;
        var oldQuilt = quilt;
        quilt = newQuilt;
        //        viewPort.setBounds(quilt.bounds());
        System.out.println("SwtQuilt : " + viewPortBounds());
        quiltChangeEvent.push(new Change<>(oldQuilt, newQuilt));
    }

    //  when the viewport bounds change we have to recompute the quilt
    //  aScale will be correct since its based on the viewport bounds which have already changed

    private void recomputeQuilt() {
        setQuilt(
                chartLocker.loadQuilt(
                        viewPort.getBounds(), aScale(), appSettings, mapDisplayOptions));
    }
}

//     System.err.println("--------------------");
//        System.err.println("VP bounds change");
//        System.err.println("quilt size = " + newQuilt.maps().size());
//        //        System.err.println("old bounds = " + oldBounds);
//        System.err.println("new bounds = " + viewPort.getBounds());
//        System.err.println("adjusted display scale = " + aScale());
//        for (var map : newQuilt.maps()) {
//            System.err.println("\tmap " + map.title() + " scale " + map.cScale());
//        }