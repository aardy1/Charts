/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.enc;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;

/**
 * An ENC cell -- a list of ENC panels plus metadata.
 *
 * @param panels the panels in the cell
 * @param name the name of the cell
 * @param title the title of the cell
 * @param compilationScale the compilation scale of the cell
 * @param zipFileLocation the location of the actual data file?
 * @param location the path to the cell on the local disk drive (this needs to be generalized)
 * @param geometry the geometry of the cell (so the union of the geometries of each panel)
 * @param bounds the bounds of the cell (a rectangular area that contains all the panels)
 */
public record ENCCell(
        List<ENCPanel> panels,
        String name,
        //  the lname field
        String title,
        int cScale,
        String zipFileLocation,
        Path location,
        MultiPolygon geometry,
        ReferencedEnvelope bounds) {

    public boolean isLoaded() {
        return location.toFile().exists();
    }

    public boolean intersects(Geometry envelope) {
        return panels.stream().anyMatch(p -> p.intersects(envelope));
    }

    //  TODO -- how can we tell if two ENCs are the same? Do we need this?

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.location);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ENCCell other = (ENCCell) obj;
        return Objects.equals(this.location, other.location);
    }
}