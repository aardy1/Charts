package org.knowtiphy.shapemap.api;

import java.util.List;

/**
 * A renderable geometry of some sort.
 *
 * <p>Note: Usually forFill and forStroke are the same, but not for things like (multi) polygons.
 *
 * @param forFill the shapes used when filling this geometry
 * @param forStroke the shapes used when stroking this geometry
 */

public record RenderableGeometry(List<RenderableShape> forFill, List<RenderableShape> forStroke ) implements IRenderableGeometry{}