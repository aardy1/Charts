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
import javafx.scene.layout.VBox;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.operation.TransformException;
import org.knowtiphy.charts.UnitProfile;
import org.knowtiphy.charts.dynamics.AISModel;
import org.knowtiphy.charts.enc.ChartLocker;
import org.knowtiphy.charts.enc.ENCChart;
import org.knowtiphy.shapemap.viewmodel.MapDisplayOptions;

public class ChartViewView extends View {

	// need to work these out from screen dimensions
	private static final int width = 1300;

	private static final int height = 750;

	// private final UnitProfile unitProfile;
	//
	// private final ChartLocker chartLocker;
	//
	// private final MapDisplayOptions displayOptions;
	//
	// private final AISModel dynamics;
	//
	// private final ENCChart chart;

	public ChartViewView(UnitProfile unitProfile, ChartLocker chartLocker, MapDisplayOptions displayOptions,
			AISModel dynamics, ENCChart chart) throws NonInvertibleTransformException, TransformException {

		// this.unitProfile = unitProfile;
		// this.chartLocker = chartLocker;
		// this.displayOptions = displayOptions;
		// this.dynamics = dynamics;
		// this.chart = chart;
		//
		// // this won't be right after the info bar is done, but that will be resized
		// later
		// chart.setViewPortScreenArea(new Rectangle2D(0, 0, width, height));
		//
		// var mapSurface = makeMap();
		//
		// // var infoBar = new InfoBar(platform, toggle, chart, unitProfile,
		// // displayOptions);

		var vbox = new VBox();
		vbox.getStyleClass().add("charts");
		// VBox.setVgrow(mapSurface, Priority.ALWAYS);
		// VBox.setVgrow(infoBar, Priority.NEVER);
		vbox.setFillWidth(true);
		// vbox.getChildren().addAll(mapSurface);
		// getStylesheets().add(Splash.class.getResource("splash.css").toExternalForm());
		vbox.setPickOnBounds(false);
		setCenter(vbox);
	}

	// private ChartView makeMap() {
	// return resizeable(new ChartView(chartLocker, chart, dynamics, unitProfile,
	// displayOptions));
	// }

	@Override
	protected void updateAppBar(AppBar appBar) {
		// AppBarManager.updateAppBar(Names.CHART_VIEW_VIEW);
		appBar.setTitleText(Names.VIEW_NAME.get(Names.CHART_VIEW_VIEW));
	}

}
