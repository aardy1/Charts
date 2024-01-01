package org.knowtiphy.shapemap.view.canvas;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.knowtiphy.shapemap.model.MapViewModel;
import org.knowtiphy.shapemap.renderer.ShapeMapRenderer;
import org.knowtiphy.shapemap.renderer.context.RendererContext;
import org.knowtiphy.shapemap.view.ShapeMapBaseSkin;
import org.knowtiphy.shapemap.view.ShapeMapView;
import org.reactfx.Subscription;

import java.util.ArrayList;
import java.util.List;

/**
 * A skin for a shape map view that uses a JavaFX canvas to show an ESRI shape of layers
 * of features of some schema type.
 *
 * @param <S> the type of the schema
 * @param <F> the type of the features
 */

public class CanvasShapeMapSkin<S, F> extends ShapeMapBaseSkin<S, F>
{
  private static final double PREFERRED_WIDTH = Region.USE_COMPUTED_SIZE;

  private static final double PREFERRED_HEIGHT = Region.USE_COMPUTED_SIZE;

  private MapViewModel<S, F> mapViewModel;

  private final Color background;

  private final Pane root;

  private final BorderPane borderPane;

  private final List<Subscription> subscriptions = new ArrayList<>();

  public CanvasShapeMapSkin(
    ShapeMapView<S, F> surface, MapViewModel<S, F> mapViewModel, Color background)
  {
    super(surface);

    this.mapViewModel = mapViewModel;
    this.background = background;

    borderPane = new BorderPane();
    root = new Pane(borderPane);
    getChildren().addAll(root);
    initGraphics();
    setupListeners();
  }

  public void setMapViewModel(MapViewModel<S, F> newMapViewModel)
  {
    // unsubscribe listeners on the old map
    subscriptions.forEach(Subscription::unsubscribe);
    subscriptions.clear();

    mapViewModel = newMapViewModel;

    setupListeners();
    root.requestLayout();
  }

  private void setupListeners()
  {
    subscriptions.add(mapViewModel.layerVisibilityEvent().subscribe(b -> root.requestLayout()));
    subscriptions.add(mapViewModel.viewPortBoundsEvent().subscribe(b -> root.requestLayout()));
  }

  private void initGraphics()
  {
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
  }

  @Override
  public void layoutChildren(
    final double x, final double y, final double width, final double height)
  {
    super.layoutChildren(x, y, width, height);
    repaint();
  }

  private void repaint()
  {
    var width = (int) root.getWidth();
    var height = (int) root.getHeight();

    var canvas = new Canvas(width, height);
    var graphics = canvas.getGraphicsContext2D();
    graphics.setFill(background);
    graphics.fillRect(0, 0, width, height);

//    var zoomFactor = mapViewModel.bounds().getWidth() / (mapViewModel.viewPortBounds().getWidth
//    ());
//    var displayScale = (int) (mapViewModel.cScale() * (1 / (zoomFactor)));

    //@formatter:off
    var rendererContext = new RendererContext<>
    (
				mapViewModel.layers(),
				mapViewModel.totalRuleCount(),
				mapViewModel.viewPortBounds(),
				new Rectangle2D(0, 0, width, height),
        mapViewModel.viewPortWorldToScreen(),
        mapViewModel.viewPortScreenToWorld(),
        mapViewModel.adjustedDisplayScale(),
				mapViewModel.featureAdapter(),
				mapViewModel.renderablePolygonProvider(),
				mapViewModel.svgProvider(),
        mapViewModel.textSizeProvider()
    );
		//@formatter:on

    var renderer = new ShapeMapRenderer<>(rendererContext, graphics);
    renderer.paint();
    borderPane.setCenter(canvas);
  }
}