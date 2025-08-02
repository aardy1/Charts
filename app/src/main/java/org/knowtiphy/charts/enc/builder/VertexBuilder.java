/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc.builder;

import org.locationtech.jts.geom.Coordinate;

/**
 * @author graham
 */
public class VertexBuilder {

    public double longitude;
    public double latitude;

    public void latitude(double latitude) {
        this.latitude = latitude;
    }

    public void longitude(double longitude) {
        this.longitude = longitude;
    }

    public Coordinate build() {
        return new Coordinate(longitude, latitude);
    }
}
