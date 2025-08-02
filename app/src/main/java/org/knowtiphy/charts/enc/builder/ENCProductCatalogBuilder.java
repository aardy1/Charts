/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.enc.builder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.enc.ENCCatalog;

/**
 * An ENC product catalog -- a named/titled collection of ENC cells. (there are other fields other
 * than the name/title but we don't keep them for the moment)
 */
public class ENCProductCatalogBuilder {

    private final Path chartsDir;
    private final List<ENCCell> cells = new ArrayList<>();

    private String title;

    public ENCProductCatalogBuilder(Path chartsDir) {
        this.chartsDir = chartsDir;
    }

    public void title(String title) {
        this.title = title;
    }

    public void cell(ENCCell cell) {
        cells.add(cell);
    }

    public Path cellPath(String cellName) {
        assert title != null;
        var regionName = title.replace("ENC Product Catalog ", "").replace(" ", "_");
        return chartsDir.resolve(Paths.get(regionName, cellName));
    }

    public ENCCatalog build() {
        return new ENCCatalog(title, cells);
    }
}
