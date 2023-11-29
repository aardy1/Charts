/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.context;

import javafx.scene.image.Image;

/**
 * @author graham
 */
public interface ISVGProvider {

	Image fetch(String name, int size);

}
