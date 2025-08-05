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
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.chartlocker.ChartLocker;
import org.knowtiphy.charts.chartview.shapemapview.IShapeMapViewModel;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.model.MapModel;
import org.knowtiphy.charts.model.Quilt;
import org.knowtiphy.charts.settings.AppSettings;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IFeatureSourceIterator;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.knowtiphy.shapemap.api.ITextBoundsFunction;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.reactfx.Change;
import org.reactfx.EventSource;
import org.reactfx.EventStream;

/**
 * An ENC chart view model -- a quilt of ENC cells (loaded from a chart locker) and viewport related
 * stuff.
 */
public class ChartViewModel implements IShapeMapViewModel<SimpleFeatureType, MemFeature> {

    private static final double DEFAULT_WIDTH = 3;

    private static final double DEFAULT_HEIGHT = 3;

    //  the quilt can change to a whole new quilt object
    private Quilt<SimpleFeatureType, MemFeature> quilt;

    private final MapViewport viewPort;
    private final ChartLocker chartLocker;
    private final AppSettings appSettings;
    private final MapDisplayOptions mapDisplayOptions;
    private final IFeatureAdapter<MemFeature> featureAdapter;
    private final IRenderablePolygonProvider renderablePolygonProvider;
    private final ISVGProvider svgProvider;
    private final ITextBoundsFunction textBoundsFunction;

    private final EventSource<Change<Quilt<SimpleFeatureType, MemFeature>>> quiltChangeEvent;
    private final EventSource<Change<ReferencedEnvelope>> viewPortBoundsEvent;
    private final EventSource<Change<Boolean>> layerVisibilityEvent;

    public ChartViewModel(
            Quilt<SimpleFeatureType, MemFeature> quilt,
            MapViewport viewPort,
            ChartLocker chartLocker,
            AppSettings appSettings,
            MapDisplayOptions mapDisplayOptions,
            IFeatureAdapter<MemFeature> featureAdapter,
            IRenderablePolygonProvider renderablePolygonProvider,
            ISVGProvider svgProvider,
            ITextBoundsFunction textBoundsFunction) {

        this.quilt = quilt;
        this.viewPort = viewPort;
        this.chartLocker = chartLocker;
        this.appSettings = appSettings;
        this.mapDisplayOptions = mapDisplayOptions;
        this.featureAdapter = featureAdapter;
        this.renderablePolygonProvider = renderablePolygonProvider;
        this.svgProvider = svgProvider;
        this.textBoundsFunction = textBoundsFunction;

        quiltChangeEvent = new EventSource<>();
        viewPortBoundsEvent = new EventSource<>();
        layerVisibilityEvent = new EventSource<>();
    }

    @Override
    public List<MapModel<SimpleFeatureType, MemFeature>> maps() {
        return quilt.maps();
    }

    //  TODO -- assumes that all maps have the same CRS -- is this too strong an assumption?
    @Override
    public CoordinateReferenceSystem crs() {
        return bounds().getCoordinateReferenceSystem();
    }

    @Override
    public ReferencedEnvelope bounds() {
        return viewPort.bounds();
    }

    @Override
    public double adjustedDisplayScale() {
        return displayScale() / 2.0;
    }

    public double zoom() {
        return viewPort.zoom();
    }

    public void setZoom(double zoom) throws TransformException, NonInvertibleTransformException {
        var oldBounds = viewPort.setZoom(zoom);
        updateQuilt(oldBounds);
    }

    public void incZoom() throws TransformException, NonInvertibleTransformException {
        setZoom(zoom() + 1);
    }

    public void decZoom() throws TransformException, NonInvertibleTransformException {
        setZoom(zoom() - 1);
    }

    @Override
    public EventStream<Change<Quilt<SimpleFeatureType, MemFeature>>> quiltChangeEvent() {
        return quiltChangeEvent;
    }

    @Override
    public EventStream<Change<ReferencedEnvelope>> viewPortBoundsEvent() {
        return viewPortBoundsEvent;
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
    public ITextBoundsFunction textSizeProvider() {
        return textBoundsFunction;
    }

    @Override
    public List<IFeatureSourceIterator<MemFeature>> featuresNearXYWorld(
            double x, double y, int radius) { // throws Exception {

        var envelope = tinyPolygon(x, y, radius);

        var result = new ArrayList<IFeatureSourceIterator<MemFeature>>();

        for (var map : maps()) {
            for (var layer : map.layers()) {
                result.add(layer.featureSource().features(envelope, Double.MIN_VALUE, true));
            }
        }

        return result;
    }

    public boolean isQuilt() {
        return quilt.maps().size() > 1;
    }

    public int cScale() {
        return quilt.maps().get(0).cScale();
    }

    public double displayScale() {
        return (int) (cScale() * (1 / viewPort.zoom()));
    }

    //  TODO
    public String title() {
        return "";
    } // return maps().get(0).lName();}

    public void setViewPortBounds(ReferencedEnvelope newBounds)
            throws TransformException, NonInvertibleTransformException {

        var oldBounds = viewPort.setBounds(newBounds);
        updateQuilt(oldBounds);

        //
        //        if (isQuilt()) {
        //            //  when the viewport bounds change we have to recompute the quilt
        //            var newQuilt =
        //                    chartLocker.loadQuilt(
        //                            bounds, adjustedDisplayScale(), appSettings,
        // mapDisplayOptions);
        //            System.err.println("--------------------");
        //            System.err.println("VP bounds change");
        //            System.err.println("quilt size = " + newQuilt.maps().size());
        //            System.err.println("old bounds = " + oldBounds);
        //            System.err.println("new bounds = " + bounds);
        //            System.err.println("adjusted display scale = " + adjustedDisplayScale());
        //            for (var map : newQuilt.maps()) {
        //                System.err.println("\tmap " + map.title() + " scale " + map.cScale());
        //            }
        //            setQuilt(newQuilt);
        //        } else {
        //            viewPortBoundsEvent.push(new Change<>(oldBounds, bounds));
        //        }
    }

    private void updateQuilt(ReferencedEnvelope oldBounds) {

        System.out.println("CV updateQuilt old bounds : " + oldBounds);
        System.out.println("CV updateQuilt new bounds : " + viewPortBounds());

        //  when the viewport bounds change we have to recompute the quilt
        var newQuilt =
                chartLocker.loadQuilt(
                        viewPortBounds(), adjustedDisplayScale(), appSettings, mapDisplayOptions);
        System.err.println("--------------------");
        System.err.println("VP bounds change");
        System.err.println("quilt size = " + newQuilt.maps().size());
        System.err.println("old bounds = " + oldBounds);
        System.err.println("new bounds = " + viewPort.bounds());
        System.err.println("adjusted display scale = " + adjustedDisplayScale());
        for (var map : newQuilt.maps()) {
            System.err.println("\tmap " + map.title() + " scale " + map.cScale());
        }
        setQuilt(newQuilt);
    }

    public Affine viewPortScreenToWorld() {
        return viewPort.screenToWorld();
    }

    public Affine viewPortWorldToScreen() {
        return viewPort.worldToScreen();
    }

    public ReferencedEnvelope viewPortBounds() {
        return viewPort.bounds();
    }

    //  do we need this -- surely the screen area is computed by the widgets?
    public Rectangle2D viewPortScreenArea() {
        return viewPort.screenArea();
    }

    public void setViewPortScreenArea(Rectangle2D screenArea)
            throws TransformException, NonInvertibleTransformException {
        viewPort.setScreenArea(screenArea);
    }

    // need to make this adaptive -- like 1/2 the maxX
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

    public ReferencedEnvelope tinyPolygon(double x, double y, int radius) {
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

    public ReferencedEnvelope tinyPolygon(double x, double y) {
        return tinyPolygon(x, y, 1);
    }

    private void setQuilt(Quilt<SimpleFeatureType, MemFeature> newQuilt) {
        var oldQuilt = quilt;
        quilt = newQuilt;
        quiltChangeEvent.push(new Change<>(oldQuilt, newQuilt));
    }
}

    //  public double zoomFactor()
    //  {
    //    return bounds().getWidth() / (viewPortBounds().getWidth());
    //  }