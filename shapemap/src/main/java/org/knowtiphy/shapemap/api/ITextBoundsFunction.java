/*
 * Copyright Knowtiphy
 * All rights reserved.
 */
package org.knowtiphy.shapemap.api;

import java.util.function.BiFunction;
import javafx.geometry.Bounds;
import javafx.scene.text.Font;

/**
 * A function that computes the JavaFX bounds (bounding box) for a piece of text in a given font.
 */
public interface ITextBoundsFunction extends BiFunction<Font, String, Bounds> {}
