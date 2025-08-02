/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.enc;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;

/** An ENC cell -- a list of ENC panels plus some metadata. */
public record ENCCell(
        List<ENCPanel> panels,
        MultiPolygon geom,
        String name,
        String lname,
        int cScale,
        boolean active,
        String zipFileLocation,
        Path location) {

    public boolean isLoaded() {
        return location.toFile().exists();
    }

    public boolean intersects(Geometry envelope) {
        return panels.stream().anyMatch(p -> p.intersects(envelope));
    }

    public ReferencedEnvelope bounds() {
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
        return new ReferencedEnvelope(minX, maxX, minY, maxY, DefaultGeographicCRS.WGS84);
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
