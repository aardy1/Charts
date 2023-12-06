/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.charts.dynamics;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.tuple.Pair;
import org.geotools.geometry.Position2D;
import org.reactfx.EventSource;

import static org.knowtiphy.charts.utils.FXUtils.later;

/**
 * @author graham
 */
public class AISModel {

	// AIS event changes
	public EventSource<AISEvent> aisEvents = new EventSource<>();

	// effectively the database of boat AIS information
	private final Map<Long, AISInformation> boats = new HashMap<>();

	public AISModel() {

		// fake AIS updates
		generateFakeAISEvents();
	}

	// for generating fake AIS events
	private static final Random random = new Random();

	@SuppressWarnings("unchecked")
	private static final Pair<Long, Position2D>[] BOAT_TEST_DATA = new Pair[] { Pair.of(1L, new Position2D(-90, 30)),
			Pair.of(2L, new Position2D(-85, 25)), Pair.of(3L, new Position2D(-80, 20)) };

	private void generateFakeAISEvents() {
		var thread = new Thread(() -> {
			while (true) {
				delay(random.nextInt(20));
				var n = random.nextInt(BOAT_TEST_DATA.length);
				var boat = BOAT_TEST_DATA[n];
				var id = boat.getLeft();
				var position = boat.getRight();
				var deltaX = random.nextDouble(2) * (Math.random() < 0.5 ? -1 : 1);
				var deltaY = random.nextDouble(2) * (Math.random() < 0.5 ? -1 : 1);
				var newPos = new Position2D(position.x + deltaX, position.y + deltaY);
				BOAT_TEST_DATA[n] = Pair.of(id, newPos);
				var aisInformation = new AISInformation(id, position);
				boats.put(id, aisInformation);
				later(() -> aisEvents.push(new AISEvent(aisInformation)));
			}
		});

		thread.setDaemon(true);
		thread.start();
	}

	private static void delay(int n) {
		try {
			Thread.sleep(n * 1000);
		}
		catch (InterruptedException ex) {
			//
		}
	}

}
