/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.enc;

import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

/** An ENC Panel -- list of boundary points making up a polygon. */
public class ENCPanel {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    private int panelNumber;

    // the boundary points of the panel
    private final List<Coordinate> vertices = new ArrayList<>();

    private Polygon geom = null;

    public List<Coordinate> vertices() {
        return vertices;
    }

    public void addVertex(Coordinate vertex) {
        this.vertices.add(vertex);
    }

    public Polygon geom() {
        if (geom == null) {
            geom = createGeom();
        }

        return geom;
    }

    public boolean intersects(Geometry envelope) {
        return envelope.intersects(geom());
    }

    @Override
    public String toString() {
        return "Panel{" + "panelNumber=" + panelNumber + ", vertices=" + vertices + '}';
    }

    public Polygon createGeom() {
        var pts = new Coordinate[vertices.size()];
        for (var i = 0; i < vertices.size(); i++) {
            pts[i] = vertices.get(i);
        }

        return GEOMETRY_FACTORY.createPolygon(pts);
    }
}
