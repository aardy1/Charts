package org.knowtiphy.charts.chartview;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AvailableCatalogs
{
  public static final Map<String, URL> AVAILABLE_CATALOGS = new HashMap<>();

  static
  {
    try
    {
      AVAILABLE_CATALOGS.put("Bulgaria Inland ENC", new URL(
        "https://raw.githubusercontent.com/chartcatalogs/catalogs/master/BG_IENC_Catalog.xml"));
    }
    catch(MalformedURLException e)
    {
      throw new RuntimeException(e);
    }
  }
}