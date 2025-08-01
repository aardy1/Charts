package org.knowtiphy.charts.enc;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Naming {

    public static Path cellName(Path chartsDir, ENCCell cell) {
        return chartsDir.resolve(Paths.get(cell.regionName(), cell.name()));
    }

    public static String regionName(ENCProductCatalog catalog) {
        return catalog.title().replace("ENC Product Catalog ", "").replace(" ", "_");
    }
}
