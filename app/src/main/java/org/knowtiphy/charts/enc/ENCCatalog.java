/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.enc;

import java.util.List;
import java.util.Objects;

/**
 * An ENC (product )catalog -- a named/titled collection of ENC cells. (there are other fields other
 * than the name/title but we don't keep them for the moment)
 */
public record ENCCatalog(String title, List<ENCCell> cells) {

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.title);
        return hash;
    }

    //  TODO -- when are two catalogs the same? Do need this?
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
        final ENCCatalog other = (ENCCatalog) obj;
        return Objects.equals(this.title, other.title);
    }

    @Override
    public String toString() {
        return "Catalog{" + "title='" + title + '\'' + ", cells=" + cells + '}';
    }
}
