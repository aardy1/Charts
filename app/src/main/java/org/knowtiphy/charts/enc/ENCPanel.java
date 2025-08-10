/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.enc;

import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

/**
 * An ENC Panel -- a list of boundary points making up a polygonal area.
 *
 * @param vertices the list of boundary points
 * @param geometry the polygon geometry formed by the vertices
 */
public record ENCPanel(List<Coordinate> vertices, Polygon geometry) {

    public boolean intersects(Geometry envelope) {
        return envelope.intersects(geometry());
    }
}
