package shapemap.view;

import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;

public abstract class ShapeMapBaseSkin extends SkinBase<ShapeMapView> implements Skin<ShapeMapView> {

	private static final double MINIMUM_WIDTH = 8;

	private static final double MINIMUM_HEIGHT = 8;

	private static final double MAXIMUM_WIDTH = Double.MAX_VALUE;

	private static final double MAXIMUM_HEIGHT = Double.MAX_VALUE;

	protected ShapeMapBaseSkin(ShapeMapView surface) {
		super(surface);
	}

	@Override
	protected double computeMinWidth(final double height, final double top, final double right, final double bottom,
			final double left) {
		return MINIMUM_WIDTH;
	}

	@Override
	protected double computeMinHeight(final double width, final double top, final double right, final double bottom,
			final double left) {
		return MINIMUM_HEIGHT;
	}

	@Override
	protected double computeMaxWidth(final double width, final double top, final double right, final double bottom,
			final double left) {
		return MAXIMUM_WIDTH;
	}

	@Override
	protected double computeMaxHeight(final double width, final double top, final double right, final double bottom,
			final double left) {
		return MAXIMUM_HEIGHT;
	}

	protected ShapeMapView S() {
		return getSkinnable();
	}

}