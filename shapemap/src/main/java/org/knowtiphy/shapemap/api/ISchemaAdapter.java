/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.api;

/**
 * An adapter for a feature schema.
 *
 * @param <S> the type of the schema
 */

public interface ISchemaAdapter<S, F> {

	String name(S schema);

}
