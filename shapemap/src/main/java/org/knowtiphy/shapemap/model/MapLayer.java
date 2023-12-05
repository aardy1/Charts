package org.knowtiphy.shapemap.model;

import java.io.IOException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.renderer.FeatureTypeStyle;
import org.knowtiphy.shapemap.api.IFeature;
import org.knowtiphy.shapemap.api.IFeatureSource;
import org.knowtiphy.shapemap.api.IFeatureSourceIterator;

public class MapLayer<S, F extends IFeature> {

	private final String title;

	private boolean visible;

	private final boolean scaleLess;

	private final IFeatureSource<S, F> featureSource;

	private final FeatureTypeStyle<F> style;

	public MapLayer(String title, IFeatureSource<S, F> featureSource, FeatureTypeStyle<F> style, boolean visible,
			boolean scaleLess) {

		this.title = title;
		this.featureSource = featureSource;
		this.style = style;
		this.visible = visible;
		this.scaleLess = scaleLess;
	}

	public String title() {
		return title;
	}

	public FeatureTypeStyle<F> getStyle() {
		return style;
	}

	// public ReferencedEnvelope getBounds() {
	// try {
	// return featureSource.getBounds();
	// }
	// catch (IOException ex) {
	// return null;
	// }
	// }

	public boolean isVisible() {
		return visible;
	}

	void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isScaleLess() {
		return scaleLess;
	}

	public IFeatureSource<S, F> getFeatureSource() {
		return featureSource;
	}

	public IFeatureSourceIterator<S, F> getFeatures(ReferencedEnvelope bounds, boolean scaleLess) throws IOException {
		return getFeatureSource().features(bounds, scaleLess);
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(getClass().getName());
		buf.append("[");
		if (title != null && title.length() != 0) {
			buf.append(title());
		}
		buf.append("]");
		return buf.toString();
	}

}
