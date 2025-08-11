package org.knowtiphy.charts.chartlocker;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AvailableCatalogs {
    public static final Map<String, URL> BUILTIN_CATALOGS = new HashMap<>();

    static {
        try {
            BUILTIN_CATALOGS.put(
                    "Region 04 - Chesapeake and Delaware Bays",
                    new URL("https://www.charts.noaa.gov/ENCs/04Region_ENCProdCat.xml"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}