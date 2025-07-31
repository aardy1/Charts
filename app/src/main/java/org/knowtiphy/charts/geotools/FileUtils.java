/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.geotools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author graham
 */
public class FileUtils {
    /**
     * Get a list of paths to the shape files in a directory.
     *
     * @param dir the directory
     * @return the list of paths
     */
    public static List<String> listShapeFilePaths(Path dir) {
        try (var stream = Files.newDirectoryStream(dir, "*.shp")) {
            return StreamSupport.stream(stream.spliterator(), false)
                    .map(x -> x.toFile().getAbsolutePath())
                    .toList();
        } catch (IOException x) {
            return List.of();
        }
    }
}