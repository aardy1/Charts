package org.knowtiphy.charts.enc;

public interface ChartDownLoaderNotifier
{
  void start();

  void reading(ENCCell cell);

  void converting(ENCCell cell);

  void cleaningUp();

  void finished();
}