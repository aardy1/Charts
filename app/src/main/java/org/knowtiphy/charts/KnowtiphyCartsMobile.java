/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts;

/**
 * @author graham
 */
import com.gluonhq.attach.display.DisplayService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.application.Application;
import javafx.geometry.Dimension2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static com.gluonhq.charm.glisten.application.AppManager.HOME_VIEW;

public class KnowtiphyCartsMobile extends Application {

	private final AppManager appManager = AppManager.initialize(this::postInit);

	@Override
	public void init() {
		appManager.addViewFactory(HOME_VIEW, () -> {
			FloatingActionButton fab = new FloatingActionButton(MaterialDesignIcon.SEARCH.text,
					e -> System.out.println("Search"));

			ImageView imageView = new ImageView(new Image(KnowtiphyCartsMobile.class.getResourceAsStream("openduke.png")));

			imageView.setFitHeight(200);
			imageView.setPreserveRatio(true);

			Label label = new Label("Hello, Gluon Mobile!");
			VBox root = new VBox(20, imageView, label);
			root.setAlignment(Pos.CENTER);

			View view = new View(root) {
				@Override
				protected void updateAppBar(AppBar appBar) {
					appBar.setTitleText("Gluon Mobile");
				}
			};

			fab.showOn(view);

			return view;
		});
	}

	@Override
	public void start(Stage stage) {
		appManager.start(stage);
	}

	private void postInit(Scene scene) {
		Swatch.LIGHT_GREEN.assignTo(scene);
		scene.getStylesheets().add(KnowtiphyCartsMobile.class.getResource("styles.css").toExternalForm());

		if (Platform.isDesktop()) {
			Dimension2D dimension2D = DisplayService.create().map(DisplayService::getDefaultDimensions)
					.orElse(new Dimension2D(640, 480));
			scene.getWindow().setWidth(dimension2D.getWidth());
			scene.getWindow().setHeight(dimension2D.getHeight());
		}
	}

	public static void main(String[] args) {
		launch();
	}

}