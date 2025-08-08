package org.knowtiphy.charts.model;

import java.util.List;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.geotools.Coordinates;

/**
 * A map view model for a collection of map models quilted together.
 *
 * @param <S> the type of the schema for the map models
 * @param <F> the type of the features in the map models
 */
public class Quilt<S, F> {

    private List<MapModel<S, F>> maps;
    private ReferencedEnvelope bounds;

    public Quilt(List<MapModel<S, F>> maps) {
        this.maps = maps;
        this.bounds = Coordinates.bounds(maps);
    }

    public List<MapModel<S, F>> maps() {
        return maps;
    }

    //  tODO -- this is wrong
    public ReferencedEnvelope bounds() {
        return bounds;
    }
}
