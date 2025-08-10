/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.chartlocker;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.tuple.Pair;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.charts.enc.ENCCatalog;
import org.knowtiphy.charts.enc.ENCCatalogReader;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.geotools.Coordinates;
import org.knowtiphy.charts.map.Map;
import org.knowtiphy.charts.map.Quilt;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.settings.AppSettings;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.TopologyException;

/**
 * @author graham
 */
public class ChartLocker {

    private final Path chartsDir;

    private final ENCCellLoader cellLoader;

    private final List<ENCCatalog> availableCatalogs = new ArrayList<>();

    private final ObservableList<ENCCell> unsortedHistory = FXCollections.observableArrayList();

    private final ObservableList<ENCCell> history =
            new SortedList<>(unsortedHistory, Comparator.comparingInt(ENCCell::cScale));

    public ChartLocker(Path catalogsDir, Path chartsDir, ENCCellLoader cellLoader)
            throws IOException, XMLStreamException {

        this.chartsDir = chartsDir;
        this.cellLoader = cellLoader;

        //  load cached catalogs
        for (var catalogFile : readAvailableCatalogs(catalogsDir)) {
            var catalog = new ENCCatalogReader(chartsDir, catalogFile).read();
            availableCatalogs.add(catalog);
        }
    }

    /**
     * Find the cell with smallest compilation scale containing a given point.
     *
     * @param point the point
     * @return the cell
     */
    public ENCCell getMostDetailedCell(Point point) {

        var smallestScale = Integer.MAX_VALUE;
        ENCCell mostDetailed = null;

        for (var catalog : availableCatalogs()) {
            for (var cell : catalog.cells()) {
                if (cell.geometry().contains(point)) {
                    if (cell.cScale() < smallestScale) {
                        smallestScale = cell.cScale();
                        mostDetailed = cell;
                    }
                }
            }
        }

        return mostDetailed;
    }

    /**
     * Load a quilt of ENC charts covering a given envelope.
     *
     * @param envelope
     * @param adjustedDisplayScale
     * @param settings
     * @param mapDisplayOptions
     * @return the quilt
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public Quilt<SimpleFeatureType, MemFeature> loadQuilt(
            ReferencedEnvelope envelope,
            double adjustedDisplayScale,
            AppSettings settings,
            MapDisplayOptions mapDisplayOptions) {

        System.out.println("LOAD QUILT " + envelope);
        var quilt = computeQuiltCellGeomPairs(envelope, adjustedDisplayScale);
        System.out.println("Quilt size = " + quilt.size());
        var maps = new LinkedList<Map<SimpleFeatureType, MemFeature>>();
        for (var entry : quilt) {
            var cell = entry.getKey();
            addChartHistory(cell);
            try {
                var map = cellLoader.loadCell(cell);
                map.setGeometry(entry.getRight());
                maps.addFirst(map);
            } catch (IOException | XMLStreamException | StyleSyntaxException ex) {
                ex.printStackTrace();
            }
        }

        var bounds = Coordinates.bounds(maps);
        return new Quilt<>(maps, bounds);
    }

    public ObservableList<ENCCell> history() {
        return history;
    }

    private void addChartHistory(ENCCell cell) {
        if (!history.contains(cell)) {
            unsortedHistory.add(cell);
        }
    }

    // TODO -- needs to go away or be smarter
    public ENCCell getCell(String lname, int cscale) {

        for (var catalog : availableCatalogs) {
            for (var cell : catalog.cells()) {
                if (cell.title().equals(lname) && cell.cScale() == cscale) {
                    return cell;
                }
            }
        }

        throw new IllegalArgumentException();
    }

    public Collection<ENCCatalog> availableCatalogs() {
        return availableCatalogs;
    }

    //  TODO -- reading it twice is a bit clumsy
    public void addCatalog(URL url) throws IOException, XMLStreamException {
        //        //  read and check for no syntax issues
        //        var catalog = new ENCCatalogReader().read(url.openStream());
        //
        //        //  read and place in the catalogs directory
        //        var filePath = catalogsDir.resolve(Path.of(catalog.title() + ".xml"));
        //        try (var channel = Channels.newChannel(url.openStream()); var fileOutputStream =
        // new FileOutputStream(filePath.toFile()); var fileChannel = fileOutputStream.getChannel())
        //        {
        //            fileChannel.transferFrom(channel, 0, Long.MAX_VALUE);
        //            availableCatalogs.add(catalog);
        //        }
    }

    public void downloadCell(ENCCell cell, ENCChartDownloadNotifier notifier) throws IOException {
        ChartDownloader.downloadCell(cell, chartsDir, notifier);
    }

    private List<ENCCell> intersections(ReferencedEnvelope envelope) {

        var bounds = JTS.toGeometry(envelope);

        var result = new ArrayList<ENCCell>();
        for (var catalog : availableCatalogs()) {
            for (var cell : catalog.cells()) {
                if (cell.intersects(bounds)) {
                    result.add(cell);
                }
            }
        }

        return result;
    }

    private List<Pair<ENCCell, Geometry>> computeQuiltCellGeomPairs(
            ReferencedEnvelope viewPortBounds, double scale) {

        System.out.println("Compute QGP scale = " + scale);
        System.out.println("Compute QGP vpb = " + viewPortBounds);
        var intersections =
                intersections(viewPortBounds).stream()
                        .filter(cell -> cell.cScale() >= scale)
                        .sorted(Comparator.comparingInt(ENCCell::cScale))
                        .toList();
        System.out.println("Intersection size = " + intersections.size());
        for (var foo : intersections) {
            System.out.println(
                    foo.title()
                            + " : "
                            + foo.cScale()
                            + " :  "
                            + scale
                            + " : "
                            + (foo.cScale() > scale));
        }
        if (intersections.isEmpty()) {
            return new ArrayList<>();
        }

        var extent = JTS.toGeometry(viewPortBounds);
        //    var remaining = extent;

        //  toList yields an array list
        var cellGeomPairs = new ArrayList<Pair<ENCCell, Geometry>>();

        //  TODO -- this could be smarter, bailing when the extent is covered
        //    var cell = intersections.get(0);
        //    var geom = cell.geom().intersection(extent);
        //    quilt.add(Pair.of(cell, geom));
        //    Geometry used = cell.geom();
        //
        //    //  TODO -- this could be smarter, bailing when the extent is covered
        //    for(var i = 1; i < intersections.size(); i++)
        //    {
        //      var ithCell = intersections.get(i);
        //      var ithGeom = ithCell.geom();
        //      quilt.add(Pair.of(ithCell, ithGeom.difference(used).intersection(extent)));
        //      used = used.union(ithGeom);
        //    }
        var cell = intersections.get(0);
        var geom = cell.geometry().intersection(extent);
        cellGeomPairs.add(Pair.of(cell, geom));
        var remaining = extent.difference(geom);

        for (var i = 1; i < intersections.size(); i++) {
            if (remaining.isEmpty()) {
                break;
            }

            var ithCell = intersections.get(i);
            var ithGeom = ithCell.geometry().intersection(extent);
            cellGeomPairs.add(Pair.of(ithCell, ithGeom.intersection(remaining)));
            try {
                remaining = remaining.difference(ithGeom);
            } catch (TopologyException | IllegalArgumentException ex) {
                System.err.println("ith geom = " + ithCell.geometry().intersection(extent));
            }
        }

        return cellGeomPairs;
    }

    private static Collection<Path> readAvailableCatalogs(Path catalogsDir) throws IOException {
        try (Stream<Path> stream = Files.list(catalogsDir)) {
            return stream.filter(
                            file ->
                                    !Files.isDirectory(file)
                                            && !file.toFile().getName().equals(".DS_Store"))
                    .toList();
        }
    }
}

    //
    //    public Quilt<SimpleFeatureType, MemFeature> loadMostDetailedQuilt(
    //            Point point,
    //            double adjustedDisplayScale,
    //            AppSettings settings,
    //            MapDisplayOptions mapDisplayOptions) {
    //
    //        var mostDetailed = mostDetailedCell(point);
    //        System.out.println("Most detailed cell : " + mostDetailed);
    //        return loadQuilt(mostDetailed.bounds(), mostDetailed.cScale(), settings,
    // mapDisplayOptions);
    //    }
