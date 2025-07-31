/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

/**
 * @author graham
 */
public class Vertex {
    private double longitude;

    private double latitude;

    public double longitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double latitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Vertex{" + "longitude=" + longitude + ", lattitude=" + latitude + '}';
    }
}