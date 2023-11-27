package org.knowtiphy.shapemap.viewmodel;

import java.io.IOException;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.knowtiphy.shapemap.renderer.FeatureTypeStyle;

public class MapLayer {

	private final String title;

	private boolean visible;

	private final boolean scaleLess;

	private final FeatureSource<SimpleFeatureType, SimpleFeature> featureSource;

	private final FeatureTypeStyle style;

	public MapLayer(String title, FeatureSource<SimpleFeatureType, SimpleFeature> featureSource, FeatureTypeStyle style,
			boolean visible, boolean scaleLess) {

		this.title = title;
		this.featureSource = featureSource;
		this.style = style;
		this.visible = visible;
		this.scaleLess = scaleLess;
	}

	public String title() {
		return title;
	}

	public FeatureTypeStyle getStyle() {
		return style;
	}

	public ReferencedEnvelope getBounds() {
		try {
			return featureSource.getBounds();
		}
		catch (IOException ex) {
			return null;
		}
	}

	public boolean isVisible() {
		return visible;
	}

	void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isScaleLess() {
		return scaleLess;
	}

	public FeatureSource<SimpleFeatureType, SimpleFeature> getFeatureSource() {
		return featureSource;
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
