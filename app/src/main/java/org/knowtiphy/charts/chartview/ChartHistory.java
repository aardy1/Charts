/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.chartview;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.knowtiphy.charts.enc.ChartDescription;

/**
 * @author graham
 */
public class ChartHistory
{

  private final ObservableList<ChartDescription> history = FXCollections.observableArrayList();

  public void addChart(ChartDescription chart)
  {
    if(!history.contains(chart))
    {
      history.add(chart);
    }
  }

  public ObservableList<ChartDescription> history()
  {
    return history;
  }

}