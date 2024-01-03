package org.knowtiphy.shapemap.view.canvas;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.knowtiphy.shapemap.model.MapModel;
import org.knowtiphy.shapemap.model.MapViewModel;
import org.knowtiphy.shapemap.renderer.ShapeMapRenderer;
import org.knowtiphy.shapemap.renderer.context.RendererContext;
import org.knowtiphy.shapemap.view.ShapeMapBaseSkin;
import org.knowtiphy.shapemap.view.ShapeMapView;

/**
 * A skin for a shape map view that uses a JavaFX canvas to show an ESRI shape map of layers
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

  private final StackPane root = new StackPane();

  public CanvasShapeMapSkin(
    ShapeMapView<S, F> surface, MapViewModel<S, F> mapViewModel, Color background)
  {
    super(surface);

    this.mapViewModel = mapViewModel;
    this.background = background;

    for(var i = 0; i < mapViewModel.maps().size(); i++)
    {
      root.getChildren().add(new BorderPane());
    }

    getChildren().addAll(root);
    initGraphics();
  }

  public void setMapViewModel(MapViewModel<S, F> newMapViewModel)
  {
    mapViewModel = newMapViewModel;
    root.requestLayout();
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
    System.err.println("layout children");
    super.layoutChildren(x, y, width, height);
    repaint();
  }

  private void repaint()
  {
    var width = (int) root.getWidth();
    var height = (int) root.getHeight();

    var removeAdd = mapViewModel.maps().size() - root.getChildren().size();
    System.err.println("Need panes " + removeAdd);
    if(removeAdd > 0)
    {
      System.err.println("Adding panes " + removeAdd);
      for(var i = root.getChildren().size(); i < removeAdd; i++)
      {
        root.getChildren().add(new BorderPane());
      }
    }
    else if(removeAdd < 0)
    {
      System.err.println("Removing panes " + removeAdd);
      root.getChildren().remove(-removeAdd, root.getChildren().size());
    }

    int which = 0;
    for(MapModel<S, F> map : mapViewModel.maps())
    {
      var canvas = new Canvas(width, height);
      var graphics = canvas.getGraphicsContext2D();
      graphics.setFill(background);
      graphics.fillRect(0, 0, width, height);

      //@formatter:off
    var rendererContext = new RendererContext<>
    (
        map.layers(),
        map.totalRuleCount(),
				mapViewModel.viewPortBounds(),
        //  TODO -- shouldn't this be in the viewport?
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
      ((BorderPane) root.getChildren().get(which)).setCenter(canvas);
      which++;
    }
  }
}