/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.enc.builder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.knowtiphy.charts.enc.ENCCatalog;
import org.knowtiphy.charts.enc.ENCCell;

/** A builder for ENC (product) catalogs. */
public class ENCCatalogBuilder {

    private final Path chartsDir;

    private String title;
    private final List<ENCCell> cells;

    public ENCCatalogBuilder(Path chartsDir) {
        this.chartsDir = chartsDir;
        cells = new ArrayList<>();
    }

    /**
     * Set the title of the catalog being built.
     *
     * @param title the cell title
     * @return the builder
     */
    public ENCCatalogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Add a cell to the list of cells of the catalog being built.
     *
     * @param cell the cell
     * @return the builder
     */
    public ENCCatalogBuilder addCell(ENCCell cell) {
        cells.add(cell);
        return this;
    }

    // TODO -- this is a total hack that needs to be factored out
    public Path cellPath(String cellName) {
        assert title != null;
        var regionName = title.replace("ENC Product Catalog ", "").replace(" ", "_");
        return chartsDir.resolve(Paths.get(regionName, cellName));
    }

    /**
     * Build the catalog as an ENCCatalog.
     *
     * @return the catalog.
     */
    public ENCCatalog build() {
        return new ENCCatalog(title, cells);
    }
}
