package org.knowtiphy.charts.chartview.view.canvas;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.geotools.geometry.jts.JTS;
import org.knowtiphy.charts.chartview.shapemapview.IShapeMapViewModel;
import org.knowtiphy.charts.model.MapModel;
import org.knowtiphy.shapemap.api.RenderingContext;
import org.knowtiphy.charts.chartview.RendererUtilities;
import org.knowtiphy.shapemap.renderer.ShapeMapRenderer;
import org.knowtiphy.shapemap.renderer.Transformation;

/**
 * NO LONGER USED
 *
 * <p>A skin for a shape map view that uses a JavaFX canvas to show an ESRI shape map of layers of
 * features of some schema type.
 *
 * @param <S> the type of the schema
 * @param <F> the type of the features
 */
public class CanvasShapeMapSkin<S, F> extends ShapeMapBaseSkin<S, F> {

    private static int count = 0;
    private static final double PREFERRED_WIDTH = Region.USE_COMPUTED_SIZE;

    private static final double PREFERRED_HEIGHT = Region.USE_COMPUTED_SIZE;

    private final IShapeMapViewModel<S, F> viewModel;

    private final Color background;

    private final StackPane root = new StackPane();

    public CanvasShapeMapSkin(
            ShapeMapControl<S, F> surface, IShapeMapViewModel<S, F> viewModel, Color background) {
        super(surface);

        this.viewModel = viewModel;
        this.background = background;

        for (MapModel<S, F> map : viewModel.maps()) {
            root.getChildren().add(new BorderPane());
        }

        getChildren().addAll(root);
        initGraphics();
    }

    private void initGraphics() {
        if (Double.compare(S().getPrefWidth(), 0.0) <= 0
                || Double.compare(S().getPrefHeight(), 0.0) <= 0
                || Double.compare(S().getWidth(), 0.0) <= 0
                || Double.compare(S().getHeight(), 0.0) <= 0) {
            if (S().getPrefWidth() > 0 && S().getPrefHeight() > 0) {
                S().setPrefSize(S().getPrefWidth(), S().getPrefHeight());
            } else {
                S().setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }
    }

    @Override
    public void layoutChildren(
            final double x, final double y, final double width, final double height) {
        System.err.println("layout children : " + width + " : " + height);
        super.layoutChildren(x, y, width, height);
        repaint(width, height);
    }

    private void repaint(final double width, final double height) {
        //        var width = (int) root.getWidth();
        //        var height = (int) root.getHeight();
        count++;
        System.out.println("repaint " + count + " : " + width + " : " + height);
        //        System.out.println(
        //                "SA dimensions "
        //                        + viewModel.getScreenAreaWidth()
        //                        + " : "
        //                        + viewModel.getScreenAreaHeight());

        if (viewModel.maps().isEmpty()) {
            System.out.println("EMPTY");

            var canvas = new Canvas(width, height);
            var graphics = canvas.getGraphicsContext2D();
            graphics.setFill(background);
            graphics.fillRect(0, 0, width, height);
            root.getChildren().add(new BorderPane(canvas));
            return;
        }

        //    var removeAdd = viewModel.maps().size() - root.getChildren().size();
        //    System.err.println("Pane calculation ");
        //    System.err.println("\tnum maps " + viewModel.maps().size());
        //    System.err.println("\tnum panes " + root.getChildren().size());
        //    System.err.println("\tremoveAdd " + removeAdd);
        //    if(removeAdd > 0)
        //    {
        //      System.err.println("Adding panes " + removeAdd);
        //      for(var i = 0; i < removeAdd; i++)
        //      {
        //        root.getChildren().add(new BorderPane());
        //      }
        //    }
        //    else if(removeAdd < 0)
        //    {
        //      System.err.println("Removing panes " + removeAdd);
        //      root.getChildren().remove(root.getChildren().size() + removeAdd,
        // root.getChildren().size());
        //    }
        root.getChildren().clear();
        int which = 0;
        //    assert root.getChildren().size() == viewModel.maps().size();
        try {
            var foo =
                    RendererUtilities.worldToScreenTransform(
                            viewModel.viewPortBounds(),
                            new Rectangle2D(0, 0, width, height),
                            viewModel.crs());
            var overallWts = new Transformation(foo);
            overallWts.apply(viewModel.viewPortBounds().getMinX(), viewModel.viewPortBounds().getMaxY());
            System.err.println(overallWts.getX() + " , " + overallWts.getY());

            if (true) {
                var canvas = new Canvas(width, height);
                var graphics = canvas.getGraphicsContext2D();
                graphics.setFill(background);
                graphics.fillRect(0, 0, width, height);
                var b = new BorderPane();
                b.setCenter(canvas);
                root.getChildren().add(b);

                for (var model : viewModel.maps()) {
                    System.err.println("which = " + which);
                    try {
                        var rendererContext =
                                new RenderingContext<>(
                                        model.layers(),
                                        model.totalRuleCount(),
                                        JTS.toEnvelope(model.geometry()),
                                        //  TODO -- shouldn't this be in the viewport?
                                        new Rectangle2D(0, 0, width, height),
                                        foo,
                                        foo.createInverse(),
                                        //        wts,
                                        //      wts.createInverse(),
                                        viewModel.adjustedDisplayScale(),
                                        viewModel.featureAdapter(),
                                        viewModel.renderablePolygonProvider(),
                                        viewModel.svgProvider(),
                                        viewModel.textSizeProvider());

                        var renderer = new ShapeMapRenderer<>(rendererContext, graphics);
                        renderer.paint();
                        //      ((BorderPane) root.getChildren().get(which)).setCenter(canvas);
                        which++;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                for (var model : viewModel.maps()) {
                    System.err.println("which = " + which);
                    //        if(which != 1)
                    //        {
                    //          which++;
                    //          continue;
                    //        }

                    //          try
                    //          {
                    //          var envelope = model.geometry().getEnvelopeInternal();
                    //          var screenArea = screenArea(overallWts, envelope);
                    //          var wts = RendererUtilities.worldToScreenTransform(envelope,
                    // screenArea, viewModel
                    //          .crs());
                    var canvas = new Canvas(width, height);
                    var graphics = canvas.getGraphicsContext2D();
                    graphics.setFill(which == 0 ? background : Color.TRANSPARENT);
                    graphics.fillRect(0, 0, width, height);
                    var b = new BorderPane();
                    root.getChildren().add(b);

                    var rendererContext =
                            new RenderingContext<>(
                                    model.layers(),
                                    model.totalRuleCount(),
                                    JTS.toEnvelope(model.geometry()),
                                    //  TODO -- shouldn't this be in the viewport?
                                    new Rectangle2D(0, 0, width, height),
                                    foo,
                                    foo.createInverse(),
                                    //        wts,
                                    //      wts.createInverse(),
                                    viewModel.adjustedDisplayScale(),
                                    viewModel.featureAdapter(),
                                    viewModel.renderablePolygonProvider(),
                                    viewModel.svgProvider(),
                                    viewModel.textSizeProvider());

                    var renderer = new ShapeMapRenderer<>(rendererContext, graphics);
                    renderer.paint();
                    b.setCenter(canvas);
                    //      ((BorderPane) root.getChildren().get(which)).setCenter(canvas);
                    which++;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //    private Rectangle2D screenArea(Transformation tx, Envelope envelope)
    //    {
    //        tx.apply(envelope.getMinX(), envelope.getMaxY());
    //        var minX = tx.getX();
    //        var minY = tx.getY();
    //        tx.apply(envelope.getMaxX(), envelope.getMinY());
    //        return new Rectangle2D(minX, minY, tx.getX() - minX, tx.getY() - minY);
    //    }
}
