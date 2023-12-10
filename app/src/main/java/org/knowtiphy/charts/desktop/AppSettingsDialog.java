package org.knowtiphy.charts.desktop;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.BindingMode;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Background;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.glyphfont.Glyph;
import org.knowtiphy.charts.Fonts;

import java.util.Arrays;

import static org.knowtiphy.charts.utils.FXUtils.gridAlwaysGrow;
import static org.knowtiphy.charts.utils.FXUtils.gridNeverGrow;

public class AppSettingsDialog
{
  private static final int BUTTON_SIZE = 16;

  public static Stage create(Window parent, AppSettings settings)
  {
    var unitForm = unitSettings(settings);
    var aisForm = aisSettings(settings);

    var vbox = new VBox(8);

    var units = button("Units", Fonts.units(BUTTON_SIZE), x -> newPane(vbox, unitForm));
    var ais = button("AIS", Fonts.boat(BUTTON_SIZE), x -> newPane(vbox, aisForm));

    var buttons = buttonBar(units, ais);

    VBox.setVgrow(buttons, Priority.NEVER);
    VBox.setVgrow(unitForm, Priority.ALWAYS);
    VBox.setVgrow(aisForm, Priority.ALWAYS);
    vbox.getChildren().addAll(buttons, unitForm);

    var stage = new Stage();

    var scene = new Scene(vbox, 600, 400);
    stage.initOwner(parent);
    stage.setScene(scene);
    stage.sizeToScene();

    return stage;
  }

  private static void newPane(VBox box, Node node)
  {
    box.getChildren().remove(box.getChildren().size() - 1);
    box.getChildren().add(node);
  }

  private static GridPane buttonBar(Button... buttons)
  {
    var bar = new GridPane();

    bar.setHgap(10);
    bar.setPadding(new Insets(10, 0, 10, 0));

    var centeredButtons = new Node[buttons.length + 2];
    centeredButtons[0] = new Region();
    centeredButtons[centeredButtons.length - 1] = new Region();
    System.arraycopy(buttons, 0, centeredButtons, 1, buttons.length);
    bar.addRow(0, centeredButtons);

    var constraints = new ColumnConstraints[buttons.length + 2];
    constraints[0] = gridAlwaysGrow();
    constraints[constraints.length - 1] = gridAlwaysGrow();
    Arrays.fill(constraints, 1, constraints.length - 1, gridNeverGrow());
    bar.getColumnConstraints().addAll(constraints);

    return bar;
  }

  private static Button button(String text, Glyph glyph, EventHandler<ActionEvent> handler)
  {
    var button = new Button(text);
    button.setContentDisplay(ContentDisplay.TOP);
    button.setGraphic(glyph);
    button.setOnAction(handler);
    button.setBackground(Background.fill(Color.TRANSPARENT));
    return button;
  }

  private static FormRenderer unitSettings(AppSettings settings)
  {
    //@formatter:off
    var form = Form.of
    (
      Group.of
      (
        Field.ofSingleSelectionType(all(DistanceUnit.values()), settings.distanceUnit).label("Distance"),
        Field.ofSingleSelectionType(all(SpeedUnit.values()), settings.speedUnit).label("Speed"),
        Field.ofSingleSelectionType(all(DepthUnit.values()), settings.depthUnit).label("Depth"),
        Field.ofSingleSelectionType(all(TemperatureUnit.values()), settings.temperatureUnit).label("Temperature")
      ),
      Group.of
      (
        Field.ofSingleSelectionType(all(LatLongFormat.values()), settings.latLongFormat).label("Lat/Long")
      )
    ).binding(BindingMode.CONTINUOUS);
    //@formatter:on

    return new FormRenderer(form);
  }

  private static FormRenderer aisSettings(AppSettings settings)
  {
    //@formatter:off
    var form = Form.of(
      Group.of(
        Field.ofDoubleType(settings.cogPredictorLengthMin).label("COG Predictor Length (min)")));
    //@formatter:on
    return new FormRenderer(form);
  }

  private static <T> SimpleListProperty<T> all(T[] possibleValues)
  {
    return new SimpleListProperty<>(FXCollections.observableArrayList(possibleValues));
  }
}

//  private static Category aisSettings(AppSettings settings)
//  {
//    //@formatter:off
//    return Category.of(AIS,
//      Group.of("AIS")
//                      );
//    //@formatter:on
//  }

//  private static <T> SingleSelectionField<T> comboBox(
//    T[] possibleValues, ObjectProperty<T> preferenceProperty)
//  {
//    return Field.ofSingleSelectionType(
//      new SimpleListProperty<>(FXCollections.observableArrayList(Arrays.asList(possibleValues))),
//      preferenceProperty).render(new SimpleComboBoxControl<>());
//  }

//  private static Category unitSettings(AppSettings settings)
//  {
//    var distanceUnit = comboBox(DistanceUnit.values(), settings.distanceUnit);
//    var speedUnit = comboBox(SpeedUnit.values(), settings.speedUnit);
//    var depthUnit = comboBox(DepthUnit.values(), settings.depthUnit);
//    var temperatureUnit = comboBox(TemperatureUnit.values(), settings.temperatureUnit);
//    var latLongPref = comboBox(LatLongFormat.values(), settings.latLongFormat);
//
//    //@formatter:off
//    return Category.of(CHART_DISPLAY,
//      Group.of("Units",
//        Setting.of("Distance", distanceUnit, settings.distanceUnit),
//        Setting.of("Speed", speedUnit, settings.speedUnit),
//        Setting.of("Depth", depthUnit, settings.depthUnit),
//        Setting.of("Temperature", temperatureUnit, settings.temperatureUnit)),
//      Group.of("",
//        Setting.of("Lat/Long", latLongPref, settings.latLongFormat))
//      );
//    //@formatter:on
//  }
//
//  private static Category aisSettings(AppSettings settings)
//  {
//    //@formatter:off
//    return Category.of(AIS,
//      Group.of("AIS")
//                      );
//    //@formatter:on
//  }
//
//  private static <T> SingleSelectionField<T> comboBox(
//    T[] possibleValues, ObjectProperty<T> preferenceProperty)
//  {
//    return Field.ofSingleSelectionType(
//      new SimpleListProperty<>(FXCollections.observableArrayList(Arrays.asList(possibleValues))),
//      preferenceProperty).render(new SimpleComboBoxControl<>());
//  }

//  private static void changeTop(StackPane stackPane, Node child)
//  {
//    System.err.println("change top to " + child);
//
//    if(stackPane.getChildren().size() > 1)
//    {
//      System.err.println("AAAAA");
//      stackPane.getChildren().remove(child);
//      stackPane.getChildren().add(child);
//    }
//  }
//
//}

//@formatter:off
//    var foo = PreferencesFx.of
//    (
//      AppSettings.class,
//      unitSettings(settings),
//      aisSettings(settings)
//    )
//    .dialogTitle("Settings")
//    .instantPersistent(false)
//    .persistWindowState(true);
//    //@formatter:on

//static final String CHART_DISPLAY = "Chart Display";
//static final String AIS = "AIS";

//    var root = vbox;
//    vbox.prefHeightProperty()
//        .addListener((obs, oldVal, newVal) -> stage.setHeight(newVal.doubleValue()));
//    vbox.prefWidthProperty()
//        .addListener((obs, oldVal, newVal) -> stage.setWidth(newVal.doubleValue()));