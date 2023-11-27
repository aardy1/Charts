/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

/**
 * @author graham
 */
public class Vertex {

	private double longitude;

	private double lattitude;

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLattitude() {
		return lattitude;
	}

	public void setLattitude(double lattitude) {
		this.lattitude = lattitude;
	}

	@Override
	public String toString() {
		return "Vertex{" + "longitude=" + longitude + ", lattitude=" + lattitude + '}';
	}

}
