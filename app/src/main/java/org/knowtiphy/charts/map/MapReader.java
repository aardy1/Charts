package org.knowtiphy.charts.map;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.StreamSupport;
import javax.xml.stream.XMLStreamException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.Name;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.store.ContentFeatureSource;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.memstore.MemStore;
import org.knowtiphy.charts.memstore.StyleReader;
import org.knowtiphy.charts.ontology.S57;
import org.knowtiphy.charts.settings.AppSettings;
import org.knowtiphy.shapemap.api.FeatureGeomType;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.index.strtree.STRtree;

/** A builder for ENC charts. */
public class MapReader {

    private final AppSettings settings;

    // conversion issue SBDAREA is a bunch of points, should be a bunch of polys?
    private final StyleReader<MemFeature> styleReader;

    private final MapDisplayOptions displayOptions;

    public MapReader(
            StyleReader<MemFeature> styleReader,
            AppSettings settings,
            MapDisplayOptions displayOptions) {

        this.settings = settings;
        this.styleReader = styleReader;
        this.displayOptions = displayOptions;
    }

    public Map<MemFeature> read(ENCCell cell)
            throws IOException, XMLStreamException, StyleSyntaxException {

        var fileNames = listShapeFilePaths(cell.location());

        var map = new Map<MemFeature>(cell.bounds(), cell.cScale(), cell.title());
        var store = new MemStore();

        for (var featureTypeName : LayerOrder.LAYER_ORDER) {
            for (var fileName : fileNames) {
                if (fileName.contains(featureTypeName)) {
                    // if the file doesn't exist, is empty, or has no .shx file, ignore it
                    var file = new File(fileName);
                    if (!file.exists() || file.length() == 0) {
                        System.err.println("Skipping : " + fileName);
                    } else {
                        var fileStore = new ShapefileDataStore(new File(fileName).toURI().toURL());
                        var featureSource = fileStore.getFeatureSource();
                        map.addLayer(
                                featureTypeName, readLayer(featureTypeName, featureSource, store));
                    }
                }
            }
        }

        return map;
    }

    private Layer<MemFeature> readLayer(
            String foo, ContentFeatureSource featureSource, MemStore store)
            throws IOException, XMLStreamException, StyleSyntaxException {

        SimpleFeatureType type = null;
        var hasScale = false;
        var index = new STRtree();

        try (var coll = featureSource.getFeatures().features()) {
            while (coll.hasNext()) {
                var geoFeature = coll.next();
                if (type == null) {
                    type = geoFeature.getFeatureType();
                    assert type.getTypeName().equals(foo)
                            : (":" + type.getTypeName() + ":" + foo + ":");
                }

                var defaultGeometry = (Geometry) geoFeature.getDefaultGeometry();
                var geometryType = geometryType(defaultGeometry);
                var feature = new MemFeature(geoFeature, defaultGeometry, geometryType);

                index.insert(defaultGeometry.getEnvelopeInternal(), feature);

                var prop = feature.getProperty(S57.AT_SCAMIN);
                if (prop != null && prop.getValue() != null) {
                    hasScale = true;
                }
            }
        }

        assert type != null;

        var typeName = type.getName();
        store.addSource(type, index);
        var parsingContext = new StyleCompiler(type, settings);
        var style = styleReader.createStyle(typeName.getLocalPart(), parsingContext);
        var memSource = store.featureSource(type, !hasScale);

        return new Layer<>(memSource, style, isVisible(typeName), !hasScale);
    }

    private boolean isVisible(Name typeName) {
        return switch (typeName.getLocalPart()) {
            case S57.OC_LIGHTS -> displayOptions.getShowLights();
            case S57.OC_OFSPLF -> displayOptions.getShowPlatforms();
            case S57.OC_SOUNDG -> displayOptions.getShowSoundings();
            case S57.OC_WRECKS -> displayOptions.getShowWrecks();
            default -> true;
        };
    }

    /*
     * List all shape files in a directory.
     */
    private static List<String> listShapeFilePaths(Path dir) throws IOException {
        try (var stream = Files.newDirectoryStream(dir, "*.shp")) {
            return StreamSupport.stream(stream.spliterator(), false)
                    .map(x -> x.toFile().getAbsolutePath())
                    .toList();
        }
    }

    /*
     * Map a JTS geometry to its type as an enum (used for fast switching on geometry types, rather
     * than using string names and hence compares for geometry types).
     *
     * @param geometry the geometry
     * @return the geometry type
     */
    private static FeatureGeomType geometryType(Geometry geometry) {
        return switch (geometry.getGeometryType()) {
            case Geometry.TYPENAME_POINT -> FeatureGeomType.POINT;
            case Geometry.TYPENAME_MULTIPOINT -> FeatureGeomType.MULTI_POINT;
            case Geometry.TYPENAME_LINESTRING -> FeatureGeomType.LINE_STRING;
            case Geometry.TYPENAME_LINEARRING -> FeatureGeomType.LINEAR_RING;
            case Geometry.TYPENAME_MULTILINESTRING -> FeatureGeomType.MULTI_LINE_STRING;
            case Geometry.TYPENAME_POLYGON -> FeatureGeomType.POLYGON;
            case Geometry.TYPENAME_MULTIPOLYGON -> FeatureGeomType.MULTI_POLYGON;
            case Geometry.TYPENAME_GEOMETRYCOLLECTION -> FeatureGeomType.GEOMETRY_COLLECTION;
            default -> throw new IllegalArgumentException(geometry.getGeometryType());
        };
    }
}