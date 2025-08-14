/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.dynamics;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.lang3.tuple.Pair;
import org.geotools.geometry.Position2D;
import static org.knowtiphy.charts.utils.FXUtils.later;
import org.reactfx.EventSource;
import org.reactfx.EventStream;

/**
 * @author graham
 */
public class AISModel {

    // AIS event changes
    private final EventSource<AISEvent> aisEvents;

    // effectively the database of boat AIS information
    //    private final Map<Long, AISInformation> boats = new HashMap<>();

    private final Timer fakeTimer;

    public AISModel() {

        aisEvents = new EventSource<>();
        // fake AIS updates
        fakeTimer = new Timer("AIS", true);
        generateFakeAISEvents();
    }

    public EventStream<AISEvent> aisEvents() {
        return aisEvents;
    }

    // for generating fake AIS events
    private static final Random random = new Random();

    //  the boat database :-)
    @SuppressWarnings("unchecked")
    private static final Pair<Long, Position2D>[] BOAT_TEST_DATA =
            new Pair[] {
                Pair.of(1L, new Position2D(-90, 30)),
                Pair.of(2L, new Position2D(-85, 25)),
                Pair.of(3L, new Position2D(-80, 20))
            };

    private void generateFakeAISEvents() {
        fakeTimer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        var n = random.nextInt(BOAT_TEST_DATA.length);
                        double moveBy = 0.5;
                        var boat = BOAT_TEST_DATA[n];
                        var id = boat.getLeft();
                        var position = boat.getRight();
                        var deltaX =
                                random.nextDouble(2) * (Math.random() < 0.5 ? -moveBy : moveBy);
                        var deltaY =
                                random.nextDouble(2) * (Math.random() < 0.5 ? -moveBy : moveBy);
                        var newPos = new Position2D(position.x + deltaX, position.y + deltaY);
                        BOAT_TEST_DATA[n] = Pair.of(id, newPos);
                        var aisInformation = new AISInformation(id, position);
                        //                        boats.put(id, aisInformation);
                        later(() -> aisEvents.push(new AISEvent(aisInformation)));
                    }
                },
                3000,
                10000);
    }
}
