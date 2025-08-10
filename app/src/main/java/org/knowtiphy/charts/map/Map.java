package org.knowtiphy.charts.map;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.IMapLayer;
import org.locationtech.jts.geom.Geometry;

/**
 * A map -- map bounds and compilation scale, and a collection of map layers.
 *
 * @param <S> the type of the schema for the map layers
 * @param <F> the type of the features for the map layers
 */
public class Map<S, F> {

    private final ReferencedEnvelope bounds;

    private final int cScale;

    private final String title;

    private final List<IMapLayer<S, F, ReferencedEnvelope>> layers = new LinkedList<>();

    private final java.util.Map<String, Layer<S, F>> nameToLayer = new LinkedHashMap<>();

    private Geometry geometry;

    // possibly shouldnt be here -- but it makes for faster rendering
    private int totalRuleCount = 0;

    public Map(ReferencedEnvelope bounds, int cScale, String title) {
        this.bounds = bounds;
        this.cScale = cScale;
        this.title = title;
    }

    public ReferencedEnvelope bounds() {
        return bounds;
    }

    public int cScale() {
        return cScale;
    }

    public String title() {
        return title;
    }

    public Geometry geometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Collection<? extends IMapLayer<S, F, ReferencedEnvelope>> layers() {
        return layers;
    }

    public void addLayer(String layerName, Layer<S, F> layer) {
        assert !nameToLayer.containsKey(layerName);
        assert layerName.equals(layer.style().featureType())
                : (layerName + " : " + layer.style().featureType());
        layers.add(layer);
        nameToLayer.put(layerName, layer);
        totalRuleCount += layer.style().rules().size();
    }

    public Layer<S, F> layer(String type) {
        return nameToLayer.get(type);
    }

    public int totalRuleCount() {
        return totalRuleCount;
    }

    public CoordinateReferenceSystem crs() {
        return bounds.getCoordinateReferenceSystem();
    }
}