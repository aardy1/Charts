/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.dynamics;

import org.geotools.geometry.Position2D;

/**
 * @author graham
 */
public class AISInformation {

    private final long id;

    private final Position2D position;

    public AISInformation(long id, Position2D position) {
        this.id = id;
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public Position2D getPosition() {
        return position;
    }
}
