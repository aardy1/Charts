/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import org.knowtiphy.shapemap.api.IFeatureSourceIterator;

import java.util.Iterator;

/**
 * @author graham
 *
 * An iterator for a "collection" of MemFeatures
 */

public record MemStoreFeatureIterator(Iterator<MemFeature> iterator)
  implements IFeatureSourceIterator<MemFeature>
{
  @Override
  public synchronized boolean hasNext()
  {
    return iterator.hasNext();
  }

  @Override
  public MemFeature next()
  {
    return iterator.next();
  }

  @Override
  public void close()
  {
    // do nothing
  }
}