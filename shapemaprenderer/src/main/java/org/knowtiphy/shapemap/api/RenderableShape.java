package org.knowtiphy.shapemap.api;

/**
 * A renderable shape of some sort.
 *
 * @param xs the x coordinates of the shape (in world coordinates)
 * @param ys the x coordinates of the shape (in world coordinates)
 */
public record RenderableShape(double[] xs,  double[] ys ) {}