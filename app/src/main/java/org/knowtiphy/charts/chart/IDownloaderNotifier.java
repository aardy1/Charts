package org.knowtiphy.charts.chart;

public interface IDownloaderNotifier<T> {

    default void start(T object) {
        //  do nothing
    }

    default void reading(T object) {
        //  do nothing
    }

    default void converting(T object) {
        //  do nothing
    }

    default void cleaningUp(T object) {
        //  do nothing
    }

    default void finished(T object) {
        //  do nothing
    }
}