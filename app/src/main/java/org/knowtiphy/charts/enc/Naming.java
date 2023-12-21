package org.knowtiphy.charts.enc;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Naming
{
  public static Path cellName(Path chartsDir, ENCCell cell)
  {
    var region = regionName(cell.catalog());
    return chartsDir.resolve(Paths.get(region, cell.name()));
  }

  private static String regionName(Catalog catalog)
  {
    return catalog.title().replace("ENC Product Catalog ", "").replace(" ", "_");
  }

}