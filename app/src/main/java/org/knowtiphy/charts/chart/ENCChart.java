/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.chart;

import java.util.List;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ITextBoundsFunction;
import org.knowtiphy.shapemap.model.MapModel;
import org.knowtiphy.shapemap.model.MapViewport;
import org.knowtiphy.shapemap.model.Quilt;
import org.knowtiphy.shapemap.renderer.context.SVGCache;

/**
 * An ENC chart -- a map view model that maintains a quilt of ENC cells (loaded from a chart locker)
 * as the viewport bounds changes.
 */
public class ENCChart extends Quilt<SimpleFeatureType, MemFeature> {

    private final ChartLocker chartLocker;

    public ENCChart(
            List<MapModel<SimpleFeatureType, MemFeature>> maps,
            MapViewport viewport,
            ChartLocker chartLocker,
            IFeatureAdapter<MemFeature> featureAdapter,
            IRenderablePolygonProvider polygonProvider,
            SVGCache svgCache,
            ITextBoundsFunction textBoundsFunction) {

        super(maps, viewport, featureAdapter, polygonProvider, svgCache, textBoundsFunction);
        this.chartLocker = chartLocker;
    }

    public boolean isQuilt() {
        return maps().size() > 1;
    }

    public int cScale() {
        return maps().get(0).cScale();
    }

    //  public double zoomFactor()
    //  {
    //    return bounds().getWidth() / (viewPortBounds().getWidth());
    //  }
    public double displayScale() {
        return (int) (cScale() * (1 / zoom()));
    }

    //  TODO
    public String title() {
        return "";
    } // return maps().get(0).lName();}

    @Override
    public void setViewPortBounds(ReferencedEnvelope bounds)
            throws TransformException, NonInvertibleTransformException {
        //  when the viewport bounds change we have to recompute the quilt
        var quilt = chartLocker.loadQuilt(bounds, adjustedDisplayScale());
        System.err.println("--------------------");
        System.err.println("VP bounds change");
        System.err.println("quilt size = " + quilt.size());
        System.err.println("adjusted display scale = " + adjustedDisplayScale());
        for (var map : quilt) {
            System.err.println("\tmap " + map.title() + " scale " + map.cScale());
        }

        setMaps(quilt);
        super.setViewPortBounds(bounds);
    }
}
