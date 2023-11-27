/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.geotools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author graham
 */
public class FileUtils {

	public static List<String> readShapeFilesInDir(Path dir) throws IOException {

		try (var stream = Files.newDirectoryStream(dir, "*.shp")) {
			return StreamSupport.stream(stream.spliterator(), false).map(x -> x.toFile().getAbsolutePath())
					.collect(Collectors.toList());
		}
		catch (IOException x) {
			return List.of();
			// IOException can never be thrown by the iteration.
			// In this snippet, it can // only be thrown by newDirectoryStream.
		}
	}

}
