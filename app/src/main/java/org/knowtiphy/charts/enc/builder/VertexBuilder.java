/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc.builder;

import org.locationtech.jts.geom.Coordinate;

/** A builder for vertices in an ENC panel. */
public class VertexBuilder {

    private double longitude;
    private double latitude;

    /**
     * Set the latitude of the vertex being built.
     *
     * @param latitude the latitude
     * @return the builder
     */
    public VertexBuilder setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    /**
     * Set the longitude of the vertex being built.
     *
     * @param longitude the longitude
     * @return the builder
     */
    public VertexBuilder setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    /**
     * Build the vertex as a coordinate.
     *
     * @return the coordinate.
     */
    public Coordinate build() {
        return new Coordinate(longitude, latitude);
    }
}
