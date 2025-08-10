/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.util.Iterator;
import java.util.function.Consumer;
import org.knowtiphy.shapemap.api.IFeatureSourceIterator;

/** An iterator over a "collection" of in memory features. */
public record MemStoreFeatureIterator(Iterator<MemFeature> iterator)
        implements IFeatureSourceIterator<MemFeature> {

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public MemFeature next() {
        return iterator.next();
    }

    @Override
    public void forEachRemaining(Consumer<? super MemFeature> action) {
        iterator.forEachRemaining(action);
    }

    @Override
    public void close() throws Exception {
        //  do nothing
    }
}