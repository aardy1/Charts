/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.ios.view;

import com.gluonhq.charm.glisten.application.AppManager;
import java.util.List;
import java.util.Map;

/**
 * @author graham
 */
public class Names {

	public static final String CHART_VIEW_VIEW = AppManager.HOME_VIEW;

	public static final String MY_BOAT_VIEW = "MY_BOAT_VIEW";

	public static final String CHART_LOCKER_VIEW = "CHART_LOCKER_VIEW";

	public static final List<String> VIEW_NAMES = List.of(CHART_VIEW_VIEW, MY_BOAT_VIEW, CHART_LOCKER_VIEW);

	public static Map<String, String> VIEW_NAME = Map.of(
	//@formatter:off
			CHART_VIEW_VIEW, "Chart",
			MY_BOAT_VIEW, "My Boat",
			CHART_LOCKER_VIEW,"Chart Locker");
			//@formatter:on

}
