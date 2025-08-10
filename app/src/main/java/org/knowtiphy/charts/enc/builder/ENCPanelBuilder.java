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

/** A builder for panels in an ENC cell. */
public class ENCPanelBuilder {

    // the boundary points of the panel
    private final List<Coordinate> vertices;

    public ENCPanelBuilder() {
        vertices = new ArrayList<>();
    }

    /**
     * Add a vertex to the panel being built.
     *
     * @param vertex the vertex
     * @return the builder;
     */
    public ENCPanelBuilder addVertex(Coordinate vertex) {
        this.vertices.add(vertex);
        return this;
    }

    /**
     * Build the panel as an ENCPanel.
     *
     * @return the panel.
     */
    public ENCPanel build() {

        //  compute the geometry polygon for the panel
        var boundaryPoints = new Coordinate[vertices.size()];
        for (var i = 0; i < vertices.size(); i++) {
            boundaryPoints[i] = vertices.get(i);
        }
        var geometry = GEOMETRY_FACTORY.createPolygon(boundaryPoints);

        return new ENCPanel(vertices, geometry);
    }
}
