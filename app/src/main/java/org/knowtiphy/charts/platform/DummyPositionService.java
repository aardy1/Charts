/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.platform;

import com.gluonhq.attach.position.Parameters;
import com.gluonhq.attach.position.Position;
import com.gluonhq.attach.position.PositionService;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author graham
 */
public class DummyPositionService implements PositionService {

	private static final ReadOnlyObjectProperty<Position> POSITION_PROPERTY = new SimpleObjectProperty<>(
			new Position(Integer.MAX_VALUE, Integer.MAX_VALUE));

	@Override
	public ReadOnlyObjectProperty<Position> positionProperty() {
		return POSITION_PROPERTY;
	}

	@Override
	public Position getPosition() {
		return POSITION_PROPERTY.get();
	}

	@Override
	public void start() {
		// do nothing
	}

	@Override
	public void start(Parameters prmtrs) {
		// do nothing
	}

	@Override
	public void stop() {
		// do nothing
	}

}
