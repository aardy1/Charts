/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartview;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.layout.Pane;
import javafx.scene.transform.NonInvertibleTransformException;
import org.apache.commons.lang3.tuple.Pair;
import org.controlsfx.glyphfont.Glyph;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.Fonts;
import org.knowtiphy.charts.dynamics.AISEvent;
import org.knowtiphy.charts.dynamics.AISInformation;
import static org.knowtiphy.charts.utils.FXUtils.later;
import org.knowtiphy.shapemap.renderer.Transformation;

/**
 * @author graham
 */
public class DynamicsView extends Pane {

    private final ChartViewModel chart;

    // on screen boat glyphs
    private final Map<Long, Pair<Glyph, AISInformation>> boats;

    public DynamicsView(ChartViewModel chart) {

        this.chart = chart;
        boats = new HashMap<>();
        initGraphics();
        registerListeners();
    }

    private void initGraphics() {
        setPickOnBounds(false);
        updateBoatPositions();
    }

    private void registerListeners() {
        widthProperty().addListener(_ -> updateBoatPositions());
        heightProperty().addListener(_ -> updateBoatPositions());
        chart.quiltChangeEvent().subscribe(_ -> updateBoatPositions());
        chart.aisEvent().subscribe(event -> updateAISInformation(event));
    }

    private void updateAISInformation(AISEvent event) {

        var aisInfo = event.aisInformation();

        var id = aisInfo.getId();
        //  is it a new boat
        if (!boats.containsKey(id)) {
            var newBoat = Fonts.boat();
            boats.put(id, Pair.of(newBoat, aisInfo));
            updateBoatPosition(newBoat, aisInfo);
            later(() -> getChildren().add(newBoat));
        } else {
            var boat = boats.get(id).getLeft();
            boats.put(id, Pair.of(boat, aisInfo));
            updateBoatPosition(boat, aisInfo);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void updateBoatPosition(Glyph boat, AISInformation aisInfo) {
        // need to clip the position?
        Transformation tx;
        try {
            tx = new Transformation(chart.viewPortWorldToScreen());
        } catch (TransformException | NonInvertibleTransformException ex) {
            ex.printStackTrace(System.err);
            return;
        }

        tx.apply(aisInfo.getPosition().getX(), aisInfo.getPosition().getY());
        boat.setTranslateX(tx.getX());
        boat.setTranslateY(tx.getY());
    }

    private void updateBoatPositions() {
        for (var boat : boats.values()) {
            updateBoatPosition(boat.getLeft(), boat.getRight());
        }
    }
}
