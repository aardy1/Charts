/*
 * Copyright (c) 2016, 2021, Gluon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of Gluon, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.knowtiphy.charts.ios.view;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.knowtiphy.charts.platform.IPlatform;

public class MyBoatView extends View {

	public MyBoatView(IPlatform platform) {

		var grid = new GridPane();
		//
		// var gps = new Label("GPS");
		// initLabel(gps, 0, 0);
		// var position = platform.positionProperty().get();
		// var gpsValue = new Label(Coordinates.labelLattitude(position.getLatitude()) + "
		// : "
		// + Coordinates.labelLongitude(position.getLongitude()));
		// initLabel(gpsValue, 1, 0);
		//
		// var heel = new Label("Heel");
		// initLabel(heel, 0, 1);
		// var heelValue = new Label("Who knows");
		// initLabel(heelValue, 1, 1);
		//
		// var windSpeed = new Label("Wind Speed");
		// initLabel(windSpeed, 0, 2);
		// var windSpeedValue = new Label("Who knows");
		// initLabel(windSpeedValue, 1, 2);
		//
		// grid.getChildren().addAll(gps, gpsValue, heel, heelValue, windSpeed,
		// windSpeedValue);
		//
		// // pane.getStyleClass().add("pane");
		// //
		// getStylesheets().add(Splash.class.getResource("splash.css").toExternalForm());

		setCenter(grid);
	}

	@Override
	protected void updateAppBar(AppBar appBar) {
		// AppBarManager.updateAppBar(Names.MY_BOAT_VIEW);
		appBar.setTitleText(Names.VIEW_NAME.get(Names.MY_BOAT_VIEW));
	}

	private void initLabel(Node node, int col, int row) {
		GridPane.setConstraints(node, col, row);
		GridPane.setHgrow(node, Priority.ALWAYS);
		GridPane.setHalignment(node, HPos.LEFT);
	}

}
