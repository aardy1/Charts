package org.knowtiphy.charts.chartview;

import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.NonInvertibleTransformException;
import org.apache.commons.lang3.tuple.Pair;
import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.Glyph;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.Fonts;
import org.knowtiphy.charts.chartview.ChartView.EventModel;
import org.knowtiphy.charts.dynamics.AISEvent;
import org.knowtiphy.charts.dynamics.AISInformation;
import org.knowtiphy.charts.dynamics.AISModel;
import org.knowtiphy.charts.enc.ChartLocker;
import org.knowtiphy.charts.enc.ENCCell;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.charts.enc.event.ChartLockerEvent;
import org.knowtiphy.charts.geotools.Queries;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.ontology.S57;
import org.knowtiphy.charts.settings.UnitProfile;
import org.knowtiphy.shapemap.renderer.Transformation;
import org.knowtiphy.shapemap.renderer.context.SVGCache;
import org.knowtiphy.shapemap.style.parser.StyleSyntaxException;
import org.knowtiphy.shapemap.view.ShapeMapView;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.knowtiphy.charts.utils.FXUtils.resizeable;

public class ChartViewSkin extends SkinBase<ChartView> implements Skin<ChartView>
{

  private static final double PREFERRED_WIDTH = Region.USE_COMPUTED_SIZE;

  private static final double PREFERRED_HEIGHT = Region.USE_COMPUTED_SIZE;

  private final ShapeMapView<SimpleFeatureType, MemFeature> mapSurface;

  private final ChartLocker chartLocker;

  private ENCChart chart;

  // private final AISModel dynamics;

  private final EventModel eventModel;

  private final MapDisplayOptions displayOptions;

  private final SVGCache svgCache;

  private final List<Subscription> subscriptions = new ArrayList<>();

  // private final Pane iconsSurface;

  private final Pane coordinateGrid;

  // private final Pane aisPane;

  // boat glyphs
  private final Map<Long, Pair<AISInformation, Glyph>> boats = new HashMap<>();

  public ChartViewSkin(
    ChartView fxMap, ChartLocker chartLocker, ENCChart chrt, AISModel dynamics,
    EventModel eventModel, UnitProfile unitProfile, MapDisplayOptions displayOptions,
    SVGCache svgCache)
  {
    super(fxMap);

    this.chartLocker = chartLocker;
    this.chart = chrt;
    // this.dynamics = dynamics;
    this.eventModel = eventModel;
    this.displayOptions = displayOptions;
    this.svgCache = svgCache;

    var root = makeRoot();
    getChildren().addAll(root);

    var surfaceDragEventsPane = new Pane();
    mapSurface = makeMapSurface();
    // iconsSurface = makeIconsSurface();
    var quiltingSurface = makeQuiltingSurface();
    coordinateGrid = makeCoordinateGrid(unitProfile);
    // aisPane = makeDynamicsSurface();

    root
      .getChildren()
      .addAll(surfaceDragEventsPane, mapSurface, quiltingSurface, coordinateGrid);// ,
    // iconsSurface,
    // //
    // quiltingSurface,
    // aisPane);

    if(Double.compare(S().getPrefWidth(), 0.0) <= 0 || Double.compare(S().getPrefHeight(),
      0.0) <= 0 || Double.compare(S().getWidth(), 0.0) <= 0 || Double.compare(S().getHeight(),
      0.0) <= 0)
    {
      if(S().getPrefWidth() > 0 && S().getPrefHeight() > 0)
      {
        S().setPrefSize(S().getPrefWidth(), S().getPrefHeight());
      }
      else
      {
        S().setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
      }
    }

    eventModel.mouseEvents.feedFrom(EventStreams.eventsOf(root, MouseEvent.ANY));
    eventModel.scrollEvents.feedFrom(EventStreams.eventsOf(root, ScrollEvent.ANY));
    eventModel.zoomEvents.feedFrom(EventStreams.eventsOf(root, ZoomEvent.ANY));

    // windows on clicked, mac on pressed
    eventModel.mouseClicked
      .filter(MouseEvent::isPopupTrigger)
      .subscribe(
        event -> makeContextMenu(event).show(mapSurface, event.getScreenX(), event.getScreenY()));
    eventModel.mousePressed
      .filter(MouseEvent::isPopupTrigger)
      .subscribe(
        event -> makeContextMenu(event).show(mapSurface, event.getScreenX(), event.getScreenY()));

    subscriptions.add(chartLocker.chartEvents().subscribe(change -> updateBoats()));
    // subscriptions.add(dynamics.aisEvents.subscribe(this::updateAISInformation));

    chartLocker.chartEvents().filter(ChartLockerEvent::isUnload).subscribe(event -> {
      // unsubscribe listeners on the old chart
      subscriptions.forEach(Subscription::unsubscribe);
      subscriptions.clear();
    });

    chartLocker.chartEvents().filter(ChartLockerEvent::isLoad).subscribe(event -> {
      chart = event.chart();
      mapSurface.setMap(chart);
      setupListeners();
    });

    unitProfile.unitChangeEvents().subscribe(e -> mapSurface.requestLayout());
    setupListeners();
  }

  private StackPane makeRoot()
  {
    return new StackPane()
    {

      @Override
      public void layoutChildren()
      {
        try
        {
          // set the screen area of the viewport before laying out the children
          chart.setViewPortScreenArea(new Rectangle2D(0, 0, (int) getWidth(), (int) getHeight()));
        }
        catch(TransformException | NonInvertibleTransformException ex)
        {
          Logger.getLogger(ChartViewSkin.class.getName()).log(Level.SEVERE, null, ex);
        }

        super.layoutChildren();
      }
    };
  }

  private Pane makeCoordinateGrid(UnitProfile unitProfile)
  {
    var theGrid = resizeable(new CoordinateGrid(chartLocker, chart, unitProfile));
    theGrid.setPickOnBounds(false);
    theGrid.setMouseTransparent(true);
    return theGrid;
  }

  private Pane makeQuiltingSurface()
  {
    var theSurface = new QuiltingSurface(chartLocker, chart, displayOptions, svgCache);
    theSurface.setPickOnBounds(false);
    return theSurface;
  }

  private ShapeMapView<SimpleFeatureType, MemFeature> makeMapSurface()
  {
    var theSurface = new ShapeMapView<>(chart, Color.LIGHTGREY);
    theSurface.setMouseTransparent(true);
    return theSurface;
  }

  private void setupListeners()
  {
    subscriptions.add(DragPanZoomSupport.addPositionAtSupport(eventModel, chart));
    subscriptions.add(DragPanZoomSupport.addDragSupport(eventModel, chart));
    subscriptions.addAll(DragPanZoomSupport.addPanningSupport(eventModel, chart));
    subscriptions.add(DragPanZoomSupport.addZoomSupport(eventModel, chart));

    subscriptions.add(
      displayOptions.showGridEvents.subscribe(c -> coordinateGrid.setVisible(c.getNewValue())));

    subscriptions.add(displayOptions.showLightsEvents.subscribe(
      change -> chart.setLayerVisible(S57.OC_LIGHTS, change.getNewValue())));
    subscriptions.add(displayOptions.showPlatformEvents.subscribe(
      change -> chart.setLayerVisible(S57.OC_OFSPLF, change.getNewValue())));
    subscriptions.add(displayOptions.showWreckEvents.subscribe(
      change -> chart.setLayerVisible(S57.OC_WRECKS, change.getNewValue())));
    subscriptions.add(displayOptions.showSoundingsEvents.subscribe(
      change -> chart.setLayerVisible(S57.OC_SOUNDG, change.getNewValue())));

    // subscriptions.add(chart.viewPortBoundsEvent.subscribe(change ->
    // updateBoats()));
  }

  private Pane makeDynamicsSurface()
  {
    var pane = new Pane();
    pane.setPickOnBounds(false);
    pane.widthProperty().addListener(cl -> updateBoats());
    pane.heightProperty().addListener(cl -> updateBoats());
    return pane;
  }

  private Pane makeIconsSurface()
  {
    return new IconSurface(chartLocker, chart);
  }

  private void showInfo(MouseEvent event)
  {

    try
    {
      // TODO are these the right x and y?
      var nearby = Queries.featuresNearXYWorld(chart, event.getX(), event.getY(), 1);

      var tx = new Transformation(chart.viewPortScreenToWorld());
      tx.apply(event.getX(), event.getY());

      // this is a bit weird since surely you can do it one query?
      var textToDisplay = new StringBuilder();
      textToDisplay.append(tx.getX()).append(", ").append(tx.getY()).append("\n");

      for(var iterator : nearby)
      {
        while(iterator.hasNext())
        {
          var feature = iterator.next();
          textToDisplay.append(feature.getIdentifier()).append("\n");
//          textToDisplay.append(feature.getDefaultGeometry()).append("\n");
          for(var attr : feature.getFeatureType().getAttributeDescriptors())
          {
            if(!attr.getLocalName().equals("the_geom"))
            {
              var attrVal = feature.getAttribute(attr.getLocalName());
              if(attrVal != null && !(attrVal instanceof String x && x.isEmpty()))
              {
                textToDisplay
                  .append("\t")
                  .append(attr.getName())
                  .append(" = ")
                  .append(attrVal)
                  .append("\n");
              }
            }
          }
        }
      }

      var text = new TextArea(textToDisplay.toString());

      var popOver = new PopOver(text);
      popOver.show(mapSurface, event.getScreenX(), event.getScreenY());
    }
    catch(Exception ex)
    {
      Logger.getLogger(ChartViewSkin.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void showMaxDetail(MouseEvent event)
  {
    var envelope = Queries.tinyPolygon(chart, event.getX(), event.getY());

    ENCCell mostDetailed = null;
    var smallestScale = Integer.MAX_VALUE;

    for(var cell : chartLocker.intersections(envelope))
    {
      if(cell.cScale() < smallestScale)
      {
        smallestScale = cell.cScale();
        mostDetailed = cell;
      }
    }

    if(mostDetailed != null && mostDetailed != chart.cell())
    {
      try
      {
        chartLocker.loadChart(mostDetailed, displayOptions, svgCache);
      }
      catch(TransformException | FactoryException | NonInvertibleTransformException |
            StyleSyntaxException ex)
      {
        Logger.getLogger(ChartViewSkin.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  private ContextMenu makeContextMenu(MouseEvent mouseEvent)
  {
    var contextMenu = new ContextMenu();
    var maxDetail = new MenuItem("Max Detail Here");
    var whatsHere = new MenuItem("What's here");

    maxDetail.setOnAction((ActionEvent event) -> showMaxDetail(mouseEvent));
    whatsHere.setOnAction((ActionEvent event) -> showInfo(mouseEvent));

    contextMenu.getItems().addAll(maxDetail, whatsHere);
    return contextMenu;
  }

  private ChartView S()
  {
    return getSkinnable();
  }

  private void updateAISInformation(AISEvent event)
  {
    var asInfo = event.getAisInformation();
    var id = asInfo.getId();
    if(!boats.containsKey(id))
    {
      var newBoat = Fonts.boat();
      boats.put(id, Pair.of(asInfo, newBoat));
      setBoatPosition(newBoat, asInfo);
      // later(() -> aisPane.getChildren().add(newBoat));
    }
    else
    {
      var boat = boats.get(id).getRight();
      boats.put(id, Pair.of(asInfo, boat));
      setBoatPosition(boat, asInfo);
    }
  }

  private void setBoatPosition(Glyph boat, AISInformation aisInfo)
  {
    // need to clip the position?
    var tx = new Transformation(chart.viewPortWorldToScreen());
    tx.apply(aisInfo.getPosition().x, aisInfo.getPosition().y);
    boat.setTranslateX(tx.getX());
    boat.setTranslateY(tx.getY());
  }

  private void updateBoats()
  {
    for(var boat : boats.values())
    {
      setBoatPosition(boat.getRight(), boat.getLeft());
    }
  }

}