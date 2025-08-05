package org.knowtiphy.charts.model;

import java.util.List;

/**
 * A map view model for a collection of map models quilted together.
 *
 * @param <S> the type of the schema for the map models
 * @param <F> the type of the features in the map models
 */
public class Quilt<S, F> {

    private List<MapModel<S, F>> maps;

    public Quilt(List<MapModel<S, F>> maps) {
        this.maps = maps;
    }

    public List<MapModel<S, F>> maps() {
        return maps;
    }
}
