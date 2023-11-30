package org.knowtiphy.shapemap.renderer;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Support for font-awesome font loading.
 *
 * @author graham
 */
public class Fonts {

	private static Text TEXT = new Text();

	public static Pair<Double, Double> textSize(Font font, String s) {
		// TODO -- why is this necessary?
		if (TEXT == null)
			TEXT = new Text();

		TEXT.setText(s);
		TEXT.setFont(font);
		return Pair.of(TEXT.getBoundsInLocal().getWidth(), TEXT.getBoundsInLocal().getHeight());
	}

	public static Pair<Double, Double> textSize(Font font) {
		return textSize(font, "A");
	}

	public static Bounds textSizeFast(Font font, String s) {
		// TODO -- why is this necessary?
		if (TEXT == null)
			TEXT = new Text();

		TEXT.setText(s);
		TEXT.setFont(font);
		return TEXT.getBoundsInLocal();
	}

}