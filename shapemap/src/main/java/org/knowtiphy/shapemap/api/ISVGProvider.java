/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import javafx.scene.image.Image;

public interface ISVGProvider {

	Image get(String name, int size, double rotation);

}
