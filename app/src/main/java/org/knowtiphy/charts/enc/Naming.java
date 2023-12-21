package org.knowtiphy.charts.enc;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Naming
{
  public static Path cellName(Path chartsDir, Catalog catalog, String name)
  {
    var region = regionName(catalog);
    return chartsDir.resolve(Paths.get(region, name));
  }

  private static String regionName(Catalog catalog)
  {
    return catalog.title().replace("ENC Product Catalog ", "").replace(" ", "_");
  }

}