/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import javafx.scene.image.Image;

/** A provider of JavaFX image objects from a string name. */
public interface ISVGProvider {

    Image get(String name, int size, double rotation);
}