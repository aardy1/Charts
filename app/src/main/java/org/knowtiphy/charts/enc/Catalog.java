/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author graham
 */
public class Catalog {

	private String title;

	private final List<ENCCell> cells = new ArrayList<>();

	public void addCell(ENCCell cell) {
		cells.add(cell);
	}

	public Collection<ENCCell> getCells() {
		return cells;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Catalog{" + "cells=" + cells + '}';
	}

}
