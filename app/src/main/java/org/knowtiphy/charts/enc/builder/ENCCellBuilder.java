/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.enc.builder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import static org.knowtiphy.charts.enc.Constants.GEOMETRY_FACTORY;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.enc.ENCPanel;
import org.locationtech.jts.geom.Polygon;

/** An ENC cell -- a list of ENC panels plus some metadata. */
public class ENCCellBuilder {

    private final List<ENCPanel> panels = new ArrayList<>();

    private String name;

    private String lname;

    private int cScale;

    private boolean active;

    private String zipFileLocation;

    private Path location;

    public void name(String name) {
        this.name = name;
    }

    public void lName(String lname) {
        this.lname = lname;
    }

    public void cScale(int scale) {
        this.cScale = scale;
    }

    public void active(boolean active) {
        this.active = active;
    }

    public void zipFileLocation(String zipFileLocation) {
        this.zipFileLocation = zipFileLocation;
    }

    public void location(Path location) {
        this.location = location;
    }

    public void panel(ENCPanel panel) {
        this.panels.add(panel);
    }

    public ENCCell build() {

        //  create the cell geometry
        var polygons = new ArrayList<Polygon>();
        for (var panel : panels) {
            polygons.add(panel.geom());
        }

        return new ENCCell(
                panels,
                GEOMETRY_FACTORY.createMultiPolygon(polygons.toArray(new Polygon[0])),
                name,
                lname,
                cScale,
                active,
                zipFileLocation,
                location);
    }
}
