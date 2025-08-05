/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartlocker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.knowtiphy.charts.enc.ENCCatalog;
import org.knowtiphy.charts.enc.ENCCell;

/** A downloader of charts from the web */
public class ChartDownloader {
    private static final List<String> CONVERT_COMMAND =
            List.of(
                    "/Users/graham/anaconda3/bin/ogr2ogr",
                    "-skipfailures",
                    "-splitlistfields",
                    "-update",
                    "-append",
                    "-f",
                    "ESRI Shapefile");

    //  TODO -- refactor with next method
    public static void downloadCell(ENCCell cell, Path chartsDir, ENCChartDownloadNotifier notifier)
            throws IOException {

        notifier.start(cell);

        var downloadTo = Files.createTempDirectory(null);

        try {
            notifier.reading(cell);
            unzipFolder(new URL(cell.zipFileLocation()), downloadTo);
            notifier.converting(cell);
            convertCell(cell, chartsDir, downloadTo);
        } finally {
            notifier.cleaningUp(cell);
            if (downloadTo != null) {
                deleteDirectory(downloadTo);
            }
            notifier.finished(cell);
        }
    }

    public static void downloadCatalog(
            ENCCatalog catalog, Path chartsDir, ENCCatalogDownloadNotifier catalogNotifier)
            throws IOException {

        catalogNotifier.start(catalog);

        var downloadTo = Files.createTempDirectory(null);

        try {
            //  download the zip files referenced in the catalog
            for (var cell : catalog.cells()) {
                catalogNotifier.reading(cell);
                unzipFolder(new URL(cell.zipFileLocation()), downloadTo);
            }

            //  convert the downloaded ENC files into shape files
            for (var cell : catalog.cells()) {
                convertCell(cell, chartsDir, downloadTo);
            }
        } finally {
            catalogNotifier.cleaningUp(catalog);
            catalogNotifier.finished(catalog);
            if (downloadTo != null) {
                deleteDirectory(downloadTo);
            }
        }
    }

    private static void unzipFolder(URL url, Path target) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(url.openStream())) {
            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {

                boolean isDirectory = zipEntry.getName().endsWith(File.separator);
                // example 1.1
                // some zip stored files and folders separately
                // e.g data/
                //     data/folder/
                //     data/folder/file.txt

                Path newPath = zipSlipProtect(zipEntry, target);

                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {
                    // example 1.2
                    // some zip stored file path only, need create parent directories
                    // e.g data/folder/file.txt
                    if (newPath.getParent() != null && (Files.notExists(newPath.getParent()))) {
                        Files.createDirectories(newPath.getParent());
                    }

                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }

                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
        }
    }

    // protect zip slip attack
    private static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir) throws IOException {

        // test zip slip vulnerability
        // Path targetDirResolved = targetDir.resolve("../../" + zipEntry.getName());

        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        // make sure normalized file still has targetDir as its prefix
        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }

        return normalizePath;
    }

    private static void convertCell(ENCCell cell, Path chartsDir, Path downloadTo)
            throws IOException {
        var downloadPath =
                downloadTo.resolve(Path.of("ENC_ROOT", cell.name(), cell.name() + ".000"));
        System.err.println("download path = " + downloadPath);
        var convertedPath = cell.location(); // Naming.cellName(chartsDir, cell);
        System.err.println("target path = " + convertedPath);
        convert(downloadPath, convertedPath);
    }

    private static void convert(Path srcFile, Path targetDir) throws IOException {
        //  ensure the targetDir directory exists
        Files.createDirectories(targetDir);
        buildShapeFiles(targetDir, srcFile);
    }

    private static void buildShapeFiles(Path targetDir, Path srcFile) throws IOException {
        var command = new ArrayList<>(CONVERT_COMMAND);
        command.add(targetDir.toFile().getAbsolutePath());
        command.add(srcFile.toFile().getAbsolutePath());

        var pb = new ProcessBuilder(command).redirectErrorStream(true);

        var p = pb.start();
        var br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        //  read stdout and stderr
        while (br.readLine() != null) {
            // de nada
        }
    }

    private static void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walkFileTree(
                    directory,
                    new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(
                                Path path, BasicFileAttributes basicFileAttributes)
                                throws IOException {
                            //          System.err.println("delete file " + path);
                            Files.delete(path);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(
                                Path path, IOException ioException) throws IOException {
                            //  System.err.println("delete dir " + path);
                            Files.delete(path);
                            return FileVisitResult.CONTINUE;
                        }
                    });
        }
    }
}

// copy files, classic
                    /*try (FileOutputStream fos = new FileOutputStream(newPath.toFile())) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }*/