/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An ENC catalog.
 */

public class Catalog
{
  private String title;

  private final List<ENCCell> cells = new ArrayList<>();

  public String title()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public void addCell(ENCCell cell)
  {
    cells.add(cell);
  }

  public Collection<ENCCell> cells()
  {
    return cells;
  }

  @Override
  public String toString()
  {
    return "Catalog{" + "title='" + title + '\'' + ", cells=" + cells + '}';
  }
}