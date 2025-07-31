/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.transform.NonInvertibleTransformException;
import org.apache.commons.lang3.tuple.Pair;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.charts.enc.event.ChartLockerEvent;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.shapemap.model.MapModel;
import org.knowtiphy.shapemap.model.MapViewport;
import org.knowtiphy.shapemap.renderer.context.SVGCache;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.TopologyException;
import org.reactfx.EventSource;
import org.reactfx.EventStream;

import javax.xml.stream.XMLStreamException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author graham
 */
public class ChartLocker {
    private final Path catalogsDir;

    private final Path chartsDir;

    private final ChartLoader chartLoader;

    private final MapDisplayOptions mapDisplayOptions;

    private final EventSource<ChartLockerEvent> chartEvents = new EventSource<>();

    private final List<Catalog> availableCatalogs = new ArrayList<>();

    private final ObservableList<ENCCell> unsortedHistory = FXCollections.observableArrayList();

    private final ObservableList<ENCCell> history =
            new SortedList<>(unsortedHistory, Comparator.comparingInt(ENCCell::cScale));

    public ChartLocker(
            Path catalogsDir,
            Path chartsDir,
            ChartLoader chartLoader,
            MapDisplayOptions mapDisplayOptions)
            throws IOException, XMLStreamException {
        this.catalogsDir = catalogsDir;
        this.chartsDir = chartsDir;
        this.chartLoader = chartLoader;
        this.mapDisplayOptions = mapDisplayOptions;
        //  load cached catalogs
        for (var catalogFile : readAvailableCatalogs(catalogsDir)) {
            var catalog = new CatalogReader(chartsDir, catalogFile).read();
            availableCatalogs.add(catalog);
        }
    }

    public EventStream<ChartLockerEvent> chartEvents() {
        return chartEvents;
    }

    public List<ENCCell> intersections(ReferencedEnvelope envelope) {
        var bounds = JTS.toGeometry(envelope);

        var result = new ArrayList<ENCCell>();
        for (var catalog : availableCatalogs()) {
            for (var cell : catalog.activeCells()) {
                if (cell.intersects(bounds)) {
                    result.add(cell);
                }
            }
        }

        return result;
    }

    List<MapModel<SimpleFeatureType, MemFeature>> loadQuilt(
            ReferencedEnvelope bounds, double adjustedDisplayScale) {
        System.err.println("recompute the quilt ");
        var quilt = computeQuilt(bounds, adjustedDisplayScale);

        var maps = new LinkedList<MapModel<SimpleFeatureType, MemFeature>>();
        for (var entry : quilt) {
            var cell = entry.getKey();
            addChartHistory(cell);
            try {
                var map = chartLoader.loadMap(cell, mapDisplayOptions);
                //  TODO -- need a delegate if we have more than one chart window
                map.setGeometry(entry.getRight());
                maps.addFirst(map);
            } catch (IOException | XMLStreamException | StyleSyntaxException ex) {
                Logger.getLogger(ChartLocker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return maps;
    }

    //  only loads a top level chart from Knowtiphy charts
    public ENCChart loadChart(ReferencedEnvelope viewPortBounds, double scale, SVGCache svgCache)
            throws TransformException, NonInvertibleTransformException {
        var maps = loadQuilt(viewPortBounds, scale);
        var viewPort = new MapViewport(viewPortBounds, false);
        //  notify that the old chart is unloaded, and the new chart is available
        //    chartEvents.push(new ChartLockerEvent(ChartLockerEvent.Type.UNLOADED, null));
        //    chartEvents.push(new ChartLockerEvent(ChartLockerEvent.Type.LOADED, newChart));

        return new ENCChart(maps, viewPort, this, svgCache);
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
            for (var cell : catalog.activeCells()) {
                if (cell.lName().equals(lname) && cell.cScale() == cscale) {
                    return cell;
                }
            }
        }

        throw new IllegalArgumentException();
    }

    public Collection<Catalog> availableCatalogs() {
        return availableCatalogs;
    }

    //  TODO -- reading it twice is a bit clumsy
    public void addCatalog(URL url) throws IOException, XMLStreamException {
        //  read and check for no syntax issues
        var catalog = new CatalogReader(chartsDir, url).read();

        //  read and place in the catalogs directory
        var filePath = catalogsDir.resolve(Path.of(catalog.title() + ".xml"));
        try (var channel = Channels.newChannel(url.openStream());
                var fileOutputStream = new FileOutputStream(filePath.toFile());
                var fileChannel = fileOutputStream.getChannel()) {
            fileChannel.transferFrom(channel, 0, Long.MAX_VALUE);
            availableCatalogs.add(catalog);
        }
    }

    public void downloadChart(ENCCell cell, ChartDownloaderNotifier notifier) throws IOException {
        ChartDownloader.downloadCell(cell, chartsDir, notifier);
    }

    private List<Pair<ENCCell, Geometry>> computeQuilt(
            ReferencedEnvelope viewPortBounds, double scale) {
        System.err.println("adjusted scale = " + scale);

        var intersections =
                intersections(viewPortBounds).stream()
                        .filter(cell -> cell.cScale() >= scale)
                        .sorted(Comparator.comparingInt(ENCCell::cScale))
                        .toList();

        if (intersections.isEmpty()) {
            return new ArrayList<>();
        }

        var extent = JTS.toGeometry(viewPortBounds);
        //    var remaining = extent;

        //  toList yields an array list
        var quilt = new ArrayList<Pair<ENCCell, Geometry>>();

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
        var geom = cell.geom().intersection(extent);
        quilt.add(Pair.of(cell, geom));
        var remaining = extent.difference(geom);

        for (var i = 1; i < intersections.size(); i++) {
            if (remaining.isEmpty()) {
                break;
            }

            var ithCell = intersections.get(i);
            var ithGeom = ithCell.geom().intersection(extent);
            quilt.add(Pair.of(ithCell, ithGeom.intersection(remaining)));
            try {
                remaining = remaining.difference(ithGeom);
            } catch (TopologyException | IllegalArgumentException ex) {
                System.err.println("ith geom = " + ithCell.geom().intersection(extent));
            }
        }

        return quilt;
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