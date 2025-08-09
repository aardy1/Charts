package org.knowtiphy.charts.chartview.shapemapview;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.api.RenderingContext;
import org.knowtiphy.shapemap.renderer.ShapeMapRenderer;

//  TODO -- extract the background color into a style sheet

/**
 * A shape map view that uses a single canvas to show a quilt of ESRI shape maps of layers of
 * features of some schema type.
 *
 * @param <S> the type of the schema
 * @param <F> the type of the features
 */
public class SingleCanvasShapeMapView<S, F> extends Region {

    //  for debugging
    private static int paintCount = 0;

    private final IShapeMapViewModel<S, F> viewModel;

    private final Color background;

    private Canvas canvas;

    private GraphicsContext gctx;

    public SingleCanvasShapeMapView(IShapeMapViewModel<S, F> viewModel, Color background) {

        this.viewModel = viewModel;
        this.background = background;
        initGraphics();
        registerListeners();
    }

    private void initGraphics() {
        canvas = new Canvas(0, 0);
        gctx = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
    }

    private void registerListeners() {
        viewModel.viewPortBoundsEvent().subscribe(_ -> requestLayout());
        viewModel
                .quiltChangeEvent()
                .subscribe(
                        _ -> {
                            System.out.println("change event " + viewModel.viewPortBounds());
                            requestLayout();
                        });
        viewModel.layerVisibilityEvent().subscribe(_ -> requestLayout());
    }

    @Override
    public void layoutChildren() {
        super.layoutChildren();
        repaint();
        //        var width =
        //                viewModel
        //                        .getScreenAreaWidth(); // getWidth(); // int)
        //                                               // viewModel.getScreenAreaWidth();
        //        var height = getHeight(); // int) viewModel.getScreenAreaHeight();
        //        if (width > 0 && height > 0) {
        //            repaint(width, height);
        //   }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void repaint() { // double width, double height) {

        assert getChildren().size() == 1;

        var screenArea = viewModel.viewPortScreenArea();
        var width = screenArea.getWidth();
        var height = screenArea.getHeight();
        System.out.println("repaint ");
        System.out.println("\tpaint count = " + paintCount);
        System.out.println("\tSA w, h = " + width + ", " + height);
        System.out.println("\tRegion w, h = " + getWidth() + ", " + getHeight());

        if (width == 0 || height == 0) {
            return;
        }

        paintCount++;

        canvas.setWidth(width);
        canvas.setHeight(height);
        setCanvasBackground(width, height);

        if (viewModel.maps().isEmpty()) {
            System.out.println("EMPTY");
            return;
        }

        //  render each map from the view model into the shared canvas
        int whichMap = 0; //  for debugging
        for (var map : viewModel.maps()) {
            System.err.println("Map # = " + whichMap);
            try {
                var rendererContext =
                        new RenderingContext<S, F, ReferencedEnvelope>(
                                map.layers(),
                                map.totalRuleCount(),
                                JTS.toEnvelope(map.geometry()),
                                screenArea,
                                viewModel.viewPortWorldToScreen(),
                                viewModel.viewPortScreenToWorld(),
                                viewModel.adjustedDisplayScale(),
                                viewModel.featureAdapter(),
                                viewModel.renderablePolygonProvider(),
                                viewModel.svgProvider(),
                                viewModel.textSizeProvider());

                var renderer = new ShapeMapRenderer<>(rendererContext, gctx);
                renderer.paint();
                whichMap++;
            } catch (NonInvertibleTransformException | TransformException e) {
                e.printStackTrace();
            }
        }
    }

    private void setCanvasBackground(double width, double height) {
        gctx.setFill(background);
        gctx.fillRect(0, 0, width, height);
    }
}

// else {
//                for (var model : viewModel.maps()) {
//                    System.err.println("which = " + which);
//                    //        if(which != 1)
//                    //        {
//                    //          which++;
//                    //          continue;
//                    //        }
//
//                    //          try
//                    //          {
//                    //          var envelope = model.geometry().getEnvelopeInternal();
//                    //          var screenArea = screenArea(overallWts, envelope);
//                    //          var wts = RendererUtilities.worldToScreenTransform(envelope,
//                    // screenArea, viewModel
//                    //          .crs());
//                    var canvas = new Canvas(width, height);
//                    var graphics = canvas.getGraphicsContext2D();
//                    graphics.setFill(which == 0 ? background : Color.TRANSPARENT);
//                    graphics.fillRect(0, 0, width, height);
//                    var b = new BorderPane();
//                    this.getChildren().add(b);
//
//                    var rendererContext =
//                            new RenderingContext<>(
//                                    model.layers(),
//                                    model.totalRuleCount(),
//                                    JTS.toEnvelope(model.geometry()),
//                                    //  TODO -- shouldn't this be in the viewport?
//                                    new Rectangle2D(0, 0, width, height),
//                                    foo,
//                                    foo.createInverse(),
//                                    //        wts,
//                                    //      wts.createInverse(),
//                                    viewModel.adjustedDisplayScale(),
//                                    viewModel.featureAdapter(),
//                                    viewModel.renderablePolygonProvider(),
//                                    viewModel.svgProvider(),
//                                    viewModel.textSizeProvider());
//
//                    var renderer = new ShapeMapRenderer<>(rendererContext, graphics);
//                    renderer.paint();
//                    b.setCenter(canvas);
//                    //      ((BorderPane) root.getChildren().get(which)).setCenter(canvas);
//                    which++;
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

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
        //    assert root.getChildren().size() == viewModel.maps().size();
