/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartview.shapemapview;

import java.util.List;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.model.MapModel;
import org.knowtiphy.charts.model.Quilt;
import org.knowtiphy.shapemap.api.IFeatureAdapter;
import org.knowtiphy.shapemap.api.IFeatureSourceIterator;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.knowtiphy.shapemap.api.ISVGProvider;
import org.reactfx.Change;
import org.reactfx.EventStream;
import org.knowtiphy.shapemap.api.ITextAdapter;

/**
 * A view model for maps of features conforming to some schema.
 *
 * @param <S> the type of the schema for the map
 * @param <F> the type of the features for the map
 */
public interface IShapeMapViewModel<S, F> {

    List<MapModel<S, F>> maps();

    CoordinateReferenceSystem crs();

    ReferencedEnvelope viewPortBounds();

    Rectangle2D viewPortScreenArea();

    double aScale();

    Affine viewPortScreenToWorld() throws TransformException, NonInvertibleTransformException;

    Affine viewPortWorldToScreen() throws TransformException, NonInvertibleTransformException;

    IFeatureAdapter<F> featureAdapter();

    IRenderablePolygonProvider renderablePolygonProvider();

    ISVGProvider svgProvider();

    ITextAdapter textSizeProvider();

    List<IFeatureSourceIterator<F>> featuresNearXYWorld(double x, double y, int radius)
            throws TransformException, NonInvertibleTransformException;

    EventStream<Change<Quilt<S, F>>> quiltChangeEvent();

    EventStream<Change<ReferencedEnvelope>> viewPortBoundsEvent();

    EventStream<Change<Rectangle2D>> viewPortScreenAreaEvent();

    EventStream<Change<Boolean>> layerVisibilityEvent();
}
