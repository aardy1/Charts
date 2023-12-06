/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

/**
 * A feature in an ESRI shape map.
 */
public interface ISchemaAdapter<S> {

	String name(S schema);

}
