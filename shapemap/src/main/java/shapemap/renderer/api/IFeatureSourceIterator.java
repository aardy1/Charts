/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.renderer.api;

/**
 * @author graham
 */
public interface IFeatureSourceIterator<T, F extends IFeature> extends AutoCloseable {

	boolean hasNext() throws Exception;

	F next() throws Exception;

}
