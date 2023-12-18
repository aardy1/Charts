/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;

import java.util.function.BiFunction;

/**
 * A provider of polygons that can be rendered (have no holes).
 */

public interface ITextSizeProvider extends BiFunction<Font, String, Bounds>
{

}