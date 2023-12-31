/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author graham
 */
public class Setup
{

  private static final List<String> CONVERT_COMMAND = List.of("/Users/graham/anaconda3/bin/ogr2ogr",
    "-skipfailures", "-splitlistfields", "-update", "-append", "-f", "ESRI Shapefile");

  public static Catalog setup(Path catalogFile)
    throws XMLStreamException, IOException, InterruptedException
  {
    var catalog = new CatalogReader(null, catalogFile).read();

    var src = "/Users/graham/Documents/Charts/ENC/US_REGION08";
    var baseTarget = "/Users/graham/tmp/ENC/" + regionName(catalog.title());

    ensureDirExists(baseTarget);

    for(var cell : catalog.activeCells())
    {
      var cellTargetDir = baseTarget + "/" + cell.name().replaceAll(" ", "_")
                                                 .replaceAll(",", "_") + "_" + cell.cScale();

      ensureDirExists(cellTargetDir);

      var cellSrcFile = src + "/" + cell.name() + "/" + cell.name() + ".000";
      buildShapeFiles(cellTargetDir, cellSrcFile);
    }

    return catalog;
  }

  private static String regionName(String name)
  {
    return name.replace("ENC Product Catalog ", "").replace(" ", "_");
  }

  private static void buildShapeFiles(String cellTargetDir, String cellSrcFile) throws IOException
  {

    var command = new ArrayList<>(CONVERT_COMMAND);
    command.add(cellTargetDir);
    command.add(cellSrcFile);

    var pb = new ProcessBuilder(command).redirectErrorStream(true);

    var p = pb.start();
    var br = new BufferedReader(new InputStreamReader(p.getInputStream()));
    while(br.readLine() != null)
    {
      // de nada
    }
  }

  private static boolean ensureDirExists(String dir)
  {
    var targetDir = new File(dir);
    if(targetDir.exists())
    {
      return true;
    }
    targetDir.mkdirs();
    return false;
  }

}