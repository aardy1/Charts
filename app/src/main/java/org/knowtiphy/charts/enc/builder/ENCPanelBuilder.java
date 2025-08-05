/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.enc.builder;

import java.util.ArrayList;
import java.util.List;
import static org.knowtiphy.charts.enc.Constants.GEOMETRY_FACTORY;
import org.knowtiphy.charts.enc.ENCPanel;
import org.locationtech.jts.geom.Coordinate;

/** An ENC Panel builder. */
public class ENCPanelBuilder {

    // the boundary points of the panel
    private final List<Coordinate> vertices = new ArrayList<>();

    public void vertex(Coordinate vertex) {
        this.vertices.add(vertex);
    }

    public ENCPanel build() {
        var pts = new Coordinate[vertices.size()];
        for (var i = 0; i < vertices.size(); i++) {
            pts[i] = vertices.get(i);
        }

        return new ENCPanel(vertices, GEOMETRY_FACTORY.createPolygon(pts));
    }
}
