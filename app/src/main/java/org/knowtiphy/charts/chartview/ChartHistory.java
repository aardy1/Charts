/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartview;

import java.util.ArrayList;
import java.util.List;
import org.knowtiphy.charts.enc.ChartDescription;

/**
 * @author graham
 */
public class ChartHistory {

	private final List<ChartDescription> history = new ArrayList<>();

	public void addChart(ChartDescription chart) {
		history.add(chart);
	}

	public List<ChartDescription> history() {
		return history;
	}

}
