/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.enc;

import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

/** An ENC Panel -- a list of boundary points making up a polygon. */
public record ENCPanel(Polygon geom, List<Coordinate> vertices) {

    public boolean intersects(Geometry envelope) {
        return envelope.intersects(geom());
    }
}
