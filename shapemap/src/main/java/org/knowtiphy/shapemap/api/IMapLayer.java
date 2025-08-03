/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package org.knowtiphy.shapemap.api;

import org.knowtiphy.shapemap.api.IFeatureSource;
import org.knowtiphy.shapemap.renderer.FeatureTypeStyle;

/**
 * @author graham
 */
public interface IMapLayer<S, F> {

    IFeatureSource<S, F> featureSource();

    //
    //    public void setVisible(boolean visible) {
    //        this.visible.set(visible);
    //    }
    //
    boolean isScaleLess();
    //
    //    public EventStream<Change<Boolean>> layerVisibilityEvent() {
    //        return layerVisibilityEvent;
    //    }

    boolean isVisible();

    FeatureTypeStyle<S, F> style();
}
