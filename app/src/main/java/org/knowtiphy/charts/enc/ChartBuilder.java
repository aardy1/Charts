package org.knowtiphy.charts.enc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import javafx.scene.transform.NonInvertibleTransformException;
import javax.xml.stream.XMLStreamException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.Name;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.store.ContentFeatureSource;
import org.knowtiphy.charts.memstore.ExtraAttributes;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.memstore.MemStore;
import org.knowtiphy.charts.memstore.StyleReader;
import org.knowtiphy.charts.ontology.S57;
import org.knowtiphy.shapemap.renderer.feature.IFeature;
import org.knowtiphy.shapemap.renderer.feature.IFeatureFunction;
import org.knowtiphy.shapemap.style.parser.IParsingContext;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.viewmodel.MapDisplayOptions;
import org.knowtiphy.shapemap.viewmodel.MapLayer;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.index.strtree.STRtree;

import static org.knowtiphy.charts.geotools.FileUtils.readShapeFilesInDir;

/**
 * @author graham
 */
public class ChartBuilder {

	private final ChartLocker chartLocker;

	private final Path shapeDir;

	private final ChartDescription chartDescription;

	private final StyleReader styleReader;

	private final MapDisplayOptions displayOptions;

	private ENCChart chart;

	private MemStore store;

	public ChartBuilder(ChartLocker chartLocker, Path shapeDir, ChartDescription chartDescription,
			StyleReader styleReader, MapDisplayOptions displayOptions)
			throws TransformException, NonInvertibleTransformException {

		this.chartLocker = chartLocker;
		this.shapeDir = shapeDir;
		this.chartDescription = chartDescription;
		this.styleReader = styleReader;
		this.displayOptions = displayOptions;
	}

	public ENCChart getMap() {
		return chart;
	}

	// conversion issue SBDAREA is a bunch of points, should be a bunch of polys?
	// WRECKS -- there are lot of them

	//@formatter:off
	public static final String[] LAYER_ORDER = new String[] { //

		//	general land outline
		S57.OC_LNDARE,

		// sea areas
		S57.OC_SEAARE,
		S57.OC_DEPARE,

		S57.OC_CTNARE,
		S57.OC_UNSARE,
		S57.OC_DMPGRD,
		S57.OC_RESARE,
		S57.OC_MIPARE,

		// land areas
		S57.OC_ICEARE,

		// land features
		S57.OC_BUAARE,
		S57.OC_RIVERS,
		S57.OC_CANALS,

		// possibly poly styled sea features
		S57.OC_FAIRWY,

		// line styled sea features
		S57.OC_DEPCNT,
		S57.OC_SOUNDG,
		S57.OC_BRIDGE,
		S57.OC_CAUSWY,
		S57.OC_DYKCON,

		// point styled sea features
		S57.OC_BOYLAT,
		S57.OC_BOYSPP,
		S57.OC_BOYSAW,
		S57.OC_LIGHTS,
		S57.OC_CURENT,
		S57.OC_OBSTRN,
		S57.OC_OFSPLF,
		S57.OC_WRECKS
	};
	//@formatter:on

	private static final Set<String> SCALELESS = new HashSet<>();
	static {
		// SCALELESS.add(S57.OC_SEAARE);
		// SCALELESS.add(S57.OC_CANALS);
		// SCALELESS.add(S57.OC_BRIDGE);
		// SCALELESS.add(S57.OC_RESARE);
		// SCALELESS.add(S57.OC_MIPARE);
	}

	public ChartBuilder read() throws IOException, XMLStreamException, TransformException, FactoryException,
			NonInvertibleTransformException, StyleSyntaxException {

		var fileNames = readShapeFilesInDir(shapeDir);
		for (var include : LAYER_ORDER) {
			for (var fileName : fileNames) {
				if (fileName.contains(include)) {
					System.err.println(fileName);
					// if the file doesn't exist, is empty, or has no .shx file, ignore it
					var file = new File(fileName);
					if (!file.exists() || file.length() == 0) {
						System.err.println("Skipping : " + fileName);
					}
					else {
						var fileStore = new ShapefileDataStore(new File(fileName).toURI().toURL());
						var featureSource = fileStore.getFeatureSource();

						if (chart == null) {
							var crs = featureSource.getBounds().getCoordinateReferenceSystem();
							chart = new ENCChart(chartLocker, chartDescription, crs, displayOptions);
							store = new MemStore(chart);
						}

						chart.addLayer(readLayer(featureSource, store));
					}
				}
			}
		}

		return this;
	}

	private MapLayer<SimpleFeatureType, IFeature> readLayer(ContentFeatureSource featureSource, MemStore store)
			throws IOException, XMLStreamException, TransformException, NonInvertibleTransformException,
			FactoryException, StyleSyntaxException {

		SimpleFeatureType type = null;
		var hasScale = false;
		var index = new STRtree();

		try (var coll = featureSource.getFeatures().features()) {
			while (coll.hasNext()) {
				var geoFeature = coll.next();
				if (type == null)
					type = geoFeature.getFeatureType();

				var geom = (Geometry) geoFeature.getDefaultGeometry();

				// TODO -- get rid of this line? replaced with enum types
				ExtraAttributes.setGeomType(geoFeature);
				var feature = new MemFeature(geoFeature.getAttributes(), geoFeature.getFeatureType(),
						geoFeature.getIdentifier(), ExtraAttributes.setGeomType(geom));
				index.insert(geom.getEnvelopeInternal(), feature);

				var prop = feature.getProperty(S57.AT_SCAMIN);
				if (prop != null && prop.getValue() != null) {
					hasScale = true;
				}
			}
		}

		assert type != null;
		var typeName = type.getName();
		var scaleLess = SCALELESS.contains(type.getTypeName()) || !hasScale;
		store.addSource(type, featureSource.getBounds(), index, scaleLess);
		final var fType = type;
		var parsingContext = new IParsingContext<MemFeature>() {
			@Override
			public IFeatureFunction<MemFeature, Object> compilePropertyAccess(String name) {
				var index = fType.indexOf(name);
				return (f, g) -> f.getAttribute(index);
			}
		};

		var style = styleReader.createStyle(typeName.getLocalPart(), parsingContext);
		return new MapLayer<>(typeName.getLocalPart(), store.getFeatureSource(typeName), style, isVisible(typeName),
				scaleLess);
	}

	public boolean isVisible(Name typeName) {
		return switch (typeName.getLocalPart()) {
			case S57.OC_LIGHTS -> displayOptions.getShowLights();
			case S57.OC_OFSPLF -> displayOptions.getShowPlatforms();
			case S57.OC_SOUNDG -> displayOptions.getShowSoundings();
			case S57.OC_WRECKS -> displayOptions.getShowWrecks();
			default -> true;
		};
	}

}
// private GeometryDescriptor singlePointGeomDescriptor(SimpleFeature feature) {
//
// var fGeomDesc = feature.getDefaultGeometryProperty().getDescriptor();
// var fGeomType = fGeomDesc.getType();
//
// var newGeomType = new GeometryTypeImpl(new NameImpl("Point"), Point.class,
// fGeomType.getCoordinateReferenceSystem(), fGeomType.isAbstract(),
// fGeomType.isIdentified(),
// fGeomType.getRestrictions(), null, null);
//
// return new GeometryDescriptorImpl​(newGeomType, new NameImpl("the_geom"),
// fGeomDesc.getMinOccurs(),
// fGeomDesc.getMaxOccurs(), fGeomDesc.isNillable(), null);
// }


				// switch (geomType) {
				// case "MultiPoint" -> {
				// var newGeomDescriptor = singlePointGeomDescriptor(feature);
				// for (var i = 0; i < ((MultiPoint) geom).getNumGeometries(); i++) {
				// var ptFeature = new MemStoreFeature(feature, count, i,
				// newGeomDescriptor);
				// ptFeature.getUserData().put(GEOM_TYPE, GeomType.POINT);
				// assert ((Geometry)
				// ptFeature.getDefaultGeometry()).getGeometryType().equals("Point");
				// index.insert(((Geometry)
				// ptFeature.getDefaultGeometry()).getEnvelopeInternal(), ptFeature);
				// count++;
				// }
				// }
				// default -> {