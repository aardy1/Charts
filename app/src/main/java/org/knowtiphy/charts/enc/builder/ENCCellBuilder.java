/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.enc.builder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import static org.knowtiphy.charts.enc.Constants.GEOMETRY_FACTORY;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.enc.ENCPanel;
import org.locationtech.jts.geom.Polygon;

/** A builder for ENC cells in an ENC Catalog. */
public class ENCCellBuilder {

    private final List<ENCPanel> panels = new ArrayList<>();

    private String name;

    //  lname
    private String title;

    private int cScale;

    private boolean active;

    private String zipFileLocation;

    private Path location;

    /**
     * Add a panel to the list of panels of the cell being built.
     *
     * @param panel the panel
     * @return the builder
     */
    public ENCCellBuilder panel(ENCPanel panel) {
        this.panels.add(panel);
        return this;
    }

    /**
     * Set the name of the cell being built.
     *
     * @param name the cell name
     * @return the builder
     */
    public ENCCellBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the title (lname) of the cell being built.
     *
     * @param title the cell title
     * @return the builder
     */
    public ENCCellBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Set the cScale of the cell being built.
     *
     * @param scale the cScale
     * @return the builder
     */
    public ENCCellBuilder cScale(int scale) {
        this.cScale = scale;
        return this;
    }

    /**
     * Set whether the cell being built is active.
     *
     * @param active is the cell active or not
     * @return the builder
     */
    public ENCCellBuilder setActive(boolean active) {
        this.active = active;
        return this;
    }

    /**
     * Set the zip file location of the cell being built.
     *
     * @param zipFileLocation the zip file location.
     * @return the builder
     */
    public ENCCellBuilder zipFileLocation(String zipFileLocation) {
        this.zipFileLocation = zipFileLocation;
        return this;
    }

    /**
     * Set the file system location of the cell being built.
     *
     * @param location the location
     * @return the builder
     */
    public ENCCellBuilder location(Path location) {
        this.location = location;
        return this;
    }

    /**
     * Build the cell as an ENCCell.
     *
     * @return the cell.
     */
    public ENCCell build() {

        //  ignore inactive cells

        if (!active) return null;

        //  compute the cell geometry

        var polygons = new ArrayList<Polygon>();
        for (var panel : panels) {
            polygons.add(panel.geometry());
        }
        var geometry = GEOMETRY_FACTORY.createMultiPolygon(polygons.toArray(Polygon[]::new));

        //  compute the cell bounds

        var minX = Double.POSITIVE_INFINITY;
        var minY = Double.POSITIVE_INFINITY;
        var maxX = Double.NEGATIVE_INFINITY;
        var maxY = Double.NEGATIVE_INFINITY;

        for (var panel : panels) {
            for (var coordinate : panel.vertices()) {
                minX = Math.min(minX, coordinate.x);
                minY = Math.min(minY, coordinate.y);
                maxX = Math.max(maxX, coordinate.x);
                maxY = Math.max(maxY, coordinate.y);
            }
        }
        // TODO -- get the CRS from the cell file
        var bounds = new ReferencedEnvelope(minX, maxX, minY, maxY, DefaultGeographicCRS.WGS84);

        return new ENCCell(
                panels, name, title, cScale, zipFileLocation, location, geometry, bounds);
    }
}
