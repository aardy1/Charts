package org.knowtiphy.charts.enc;

import javafx.scene.transform.*;
import org.geotools.api.feature.simple.*;
import org.geotools.api.feature.type.*;
import org.geotools.api.referencing.*;
import org.geotools.api.referencing.operation.*;
import org.geotools.data.shapefile.*;
import org.geotools.data.store.*;
import org.knowtiphy.charts.*;
import org.knowtiphy.charts.chartview.*;
import org.knowtiphy.charts.memstore.*;
import org.knowtiphy.charts.ontology.*;
import org.knowtiphy.shapemap.model.*;
import org.knowtiphy.shapemap.style.parser.*;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.index.strtree.*;

import javax.xml.stream.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.knowtiphy.charts.geotools.FileUtils.*;

/**
 * @author graham
 */
public class ChartBuilder
{

  private final ChartLocker chartLocker;

  private final Path shapeDir;

  private final ChartDescription chartDescription;

  private final UnitProfile unitProfile;

  private final StyleReader<SimpleFeatureType, MemFeature> styleReader;

  private final MapDisplayOptions displayOptions;

  private ENCChart chart;

  private MemStore store;

  public ChartBuilder(
    ChartLocker chartLocker,
    Path shapeDir,
    ChartDescription chartDescription,
    UnitProfile unitProfile,
    StyleReader<SimpleFeatureType, MemFeature> styleReader,
    MapDisplayOptions displayOptions)
  {

    this.chartLocker = chartLocker;
    this.shapeDir = shapeDir;
    this.unitProfile = unitProfile;
    this.chartDescription = chartDescription;
    this.styleReader = styleReader;
    this.displayOptions = displayOptions;

    // displayOptions.showLightsEvents.subscribe(change -> setLayerVisible(change,
    // S57.OC_LIGHTS));
    // displayOptions.showPlatformEvents.subscribe(change -> setLayerVisible(change,
    // S57.OC_OFSPLF));
    // displayOptions.showSoundingsEvents.subscribe(change -> setLayerVisible(change,
    // S57.OC_SOUNDG));
    // displayOptions.showWreckEvents.subscribe(change -> setLayerVisible(change,
    // S57.OC_WRECKS));

  }

  public ENCChart getMap()
  {
    return chart;
  }

  // conversion issue SBDAREA is a bunch of points, should be a bunch of polys?
  // WRECKS -- there are lot of them

  // @formatter:off
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
    // @formatter:on

  private static final Set<String> SCALELESS = new HashSet<>();

  static
  {
    // SCALELESS.add(S57.OC_SEAARE);
    // SCALELESS.add(S57.OC_CANALS);
    // SCALELESS.add(S57.OC_BRIDGE);
    // SCALELESS.add(S57.OC_RESARE);
    // SCALELESS.add(S57.OC_MIPARE);
  }

  public ChartBuilder read()
    throws IOException, XMLStreamException, TransformException, FactoryException,
           NonInvertibleTransformException, StyleSyntaxException
  {

    var fileNames = readShapeFilesInDir(shapeDir);
    for(var include : LAYER_ORDER)
    {
      for(var fileName : fileNames)
      {
        if(fileName.contains(include))
        {
          System.err.println(fileName);
          // if the file doesn't exist, is empty, or has no .shx file, ignore it
          var file = new File(fileName);
          if(!file.exists() || file.length() == 0)
          {
            System.err.println("Skipping : " + fileName);
          }
          else
          {
            var fileStore = new ShapefileDataStore(
              new File(fileName).toURI().toURL());
            var featureSource = fileStore.getFeatureSource();

            if(chart == null)
            {
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

  private MapLayer<SimpleFeatureType, MemFeature> readLayer(
    ContentFeatureSource featureSource, MemStore store
                                                           )
    throws IOException, XMLStreamException, StyleSyntaxException
  {

    SimpleFeatureType type = null;
    var hasScale = false;
    var index = new STRtree();

    try(var coll = featureSource.getFeatures().features())
    {
      while(coll.hasNext())
      {
        var geoFeature = coll.next();
        if(type == null)
        {
          type = geoFeature.getFeatureType();
        }

        var geom = (Geometry) geoFeature.getDefaultGeometry();

        // TODO -- get rid of this line? replaced with enum types
        ExtraAttributes.geomType(geoFeature);
        var feature = new MemFeature(
          geoFeature.getAttributes(),
          geoFeature.getFeatureType(),
          geom,
          geoFeature.getIdentifier());
        index.insert(geom.getEnvelopeInternal(), feature);

        var prop = feature.getProperty(S57.AT_SCAMIN);
        if(prop != null && prop.getValue() != null)
        {
          hasScale = true;
        }
      }
    }

    assert type != null;
    var typeName = type.getName();
    var scaleLess = SCALELESS.contains(type.getTypeName()) || !hasScale;
    store.addSource(type, index);
    var parsingContext = new ParsingContext(type, unitProfile);
    var style = styleReader.createStyle(typeName.getLocalPart(), parsingContext);
    var memSource = store.featureSource(type);

    return new MapLayer<>(typeName.getLocalPart(), memSource, style, isVisible(typeName),
      scaleLess);
  }

  public boolean isVisible(Name typeName)
  {
    return switch(typeName.getLocalPart())
    {
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