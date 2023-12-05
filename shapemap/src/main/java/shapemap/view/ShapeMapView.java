package shapemap.view;

import java.util.List;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import shapemap.model.IMapViewModel;
import shapemap.view.canvas.CanvasShapeMapSkin;

/**
 * A map "surface" -- a control that shows a map.
 */
public class ShapeMapView extends Control {

	public enum SkinType {

		CANVAS

	}

	// CSS styling
	private static final StyleablePropertyFactory<ShapeMapView> FACTORY = new StyleablePropertyFactory<>(
			Control.getClassCssMetaData());

	private static String DEFAULT_STYLE_SHEET;

	private SkinType skinType;

	private final IMapViewModel map;

	public ShapeMapView(IMapViewModel map) {
		this(map, SkinType.CANVAS);
	}

	public ShapeMapView(IMapViewModel map, SkinType skinType) {

		this.map = map;
		this.skinType = skinType;
		getStyleClass().add("shapemap-view");
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	protected Skin createDefaultSkin() {
		return switch (skinType) {
			default -> new CanvasShapeMapSkin(this, map);
		};
	}

	@Override
	public synchronized String getUserAgentStylesheet() {

		switch (skinType) {
			case CANVAS:
			default:
				if (DEFAULT_STYLE_SHEET == null) {
					DEFAULT_STYLE_SHEET = ShapeMapView.class.getResource("canvas.css").toExternalForm();
				}
				return DEFAULT_STYLE_SHEET;
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return FACTORY.getCssMetaData();
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return FACTORY.getCssMetaData();
	}

}