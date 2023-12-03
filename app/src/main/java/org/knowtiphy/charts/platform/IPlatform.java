/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.platform;

import com.gluonhq.attach.position.Position;
import java.nio.file.Path;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * Abstraction of the underlying platform.
 */
public interface IPlatform {

	void setTitle(Stage stage, String title);

	void setWindowIcons(Stage stage, Class<?> cls);

	Path rootDir();

	Path chartsDir();

	Rectangle2D screenDimensions();

	double windowWidthCM(Region region);

	double ppi();

	double ppcm();

	ReadOnlyObjectProperty<Position> positionProperty();

	void info();

}
