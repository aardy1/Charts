/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

import java.util.Collection;

/**
 * @author graham
 */
public interface IRenderableGeometry {

    Collection<RenderableShape> forFill();

    Collection<RenderableShape> forStroke();
}
