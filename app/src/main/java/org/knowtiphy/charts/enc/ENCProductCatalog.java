/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.enc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * An ENC product catalog -- a named collection of ENC cells. (there are other fields other than the
 * name/title but we don't keep them for the moment)
 */
public class ENCProductCatalog {

    private final List<ENCCell> cells = new ArrayList<>();

    private String title;

    public String title() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addCell(ENCCell cell) {
        cells.add(cell);
    }

    public Collection<ENCCell> cells() {
        return cells;
    }

    //  TODO -- when are two catalogs the same?
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var catalog = (ENCProductCatalog) o;
        return Objects.equals(title, catalog.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    @Override
    public String toString() {
        return "Catalog{" + "title='" + title + '\'' + ", cells=" + cells + '}';
    }
}
