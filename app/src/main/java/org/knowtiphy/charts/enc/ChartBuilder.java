package org.knowtiphy.charts.enc;

import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.Name;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.store.ContentFeatureSource;
import org.knowtiphy.charts.chartview.MapDisplayOptions;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.memstore.MemStore;
import org.knowtiphy.charts.memstore.StyleReader;
import org.knowtiphy.charts.ontology.S57;
import org.knowtiphy.charts.settings.AppSettings;
import org.knowtiphy.shapemap.model.MapLayer;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.index.strtree.STRtree;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.knowtiphy.charts.geotools.FileUtils.readShapeFilesInDir;

/**
 * @author graham
 */
public class ChartBuilder
{
  private final Path shapeDir;

  private final ENCCell chartDescription;

  private final AppSettings settings;

  private final StyleReader<SimpleFeatureType, MemFeature> styleReader;

  private final MapDisplayOptions displayOptions;

  private ENCChart chart;

  private MemStore store;

  public ChartBuilder(
    Path shapeDir, ENCCell chartDescription, AppSettings settings,
    StyleReader<SimpleFeatureType, MemFeature> styleReader, MapDisplayOptions displayOptions)
  {
    this.shapeDir = shapeDir;
    this.settings = settings;
    this.chartDescription = chartDescription;
    this.styleReader = styleReader;
    this.displayOptions = displayOptions;
  }

  public ENCChart getMap()
  {
    return chart;
  }

  // conversion issue SBDAREA is a bunch of points, should be a bunch of polys?

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

        //  land features
        S57.OC_BUAARE,
        S57.OC_RIVERS,
        S57.OC_LAKARE,
        S57.OC_CANALS,

        // possibly poly styled sea features
        S57.OC_FAIRWY,

        // line styled sea features
        S57.OC_DEPCNT,
        S57.OC_SOUNDG,
        S57.OC_BRIDGE,
        S57.OC_CAUSWY,
        S57.OC_DYKCON,

        // point styled land features
        S57.OC_LNDMRK,

        // point styled sea features
        S57.OC_ACHARE,
        S57.OC_ACHBRT,
        S57.OC_BCNLAT,
        S57.OC_BCNSAW,
        S57.OC_BCNSPP,
        S57.OC_BERTHS,
        S57.OC_BOYLAT,
        S57.OC_BOYSPP,
        S57.OC_BOYSAW,
        S57.OC_LIGHTS,
        S57.OC_CURENT,
        S57.OC_OBSTRN,
        S57.OC_OFSPLF,
        S57.OC_WRECKS,
        S57.OC_RTPBCN,
        S57.OC_UWTROC
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

//    var all = new HashSet<String>();
//
//    for(var fileName : fileNames)
//    {
//      var bits = fileName.split("/");
//      var type = bits[bits.length - 1].split("\\.")[0];
//      all.add(type);
//    }

    for(var include : LAYER_ORDER)
    {
      for(var fileName : fileNames)
      {
        if(fileName.contains(include))
        {
//          System.err.println(fileName);
          // if the file doesn't exist, is empty, or has no .shx file, ignore it
          var file = new File(fileName);
          if(!file.exists() || file.length() == 0)
          {
            System.err.println("Skipping : " + fileName);
          }
          else
          {
//            var bits = fileName.split("/");
//            var type = bits[bits.length - 1].split("\\.")[0];
//            all.remove(type);
            var fileStore = new ShapefileDataStore(new File(fileName).toURI().toURL());
            var featureSource = fileStore.getFeatureSource();

            if(chart == null)
            {
              var crs = featureSource.getBounds().getCoordinateReferenceSystem();
              chart = new ENCChart(chartDescription, crs);
              store = new MemStore(chart);
            }

            chart.addLayer(readLayer(featureSource, store));
          }
        }
      }
    }

//    System.err.println("------------------------------------------------------");
//    for(var type : all)
//    {
//      System.err.println(type);
//    }
//    System.err.println("------------------------------------------------------");

    return this;
  }

  private MapLayer<SimpleFeatureType, MemFeature> readLayer(
    ContentFeatureSource featureSource, MemStore store)
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

        var feature = new MemFeature(geoFeature);
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
    var parsingContext = new StyleCompilerAdapter(type, settings);
    var style = styleReader.createStyle(typeName.getLocalPart(), parsingContext);
    var memSource = store.featureSource(type);

    return new MapLayer<>(memSource, style, isVisible(typeName), scaleLess);
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