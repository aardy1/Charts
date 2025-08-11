package org.knowtiphy.charts.map;

import java.util.List;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * A collection of maps (potentially with different cScales) quilted together to form a larger map.
 * It is assumed that the geometry of each map in the quilt does not intersect the geometry of any
 * other map in the quilt.
 *
 * @param <S> the type of the schema for the maps
 * @param <F> the type of the features in the maps
 * @param maps the maps that make up the quilt
 * @param bounds the bounds of the quilt of maps
 */
public record Quilt< F>(List<Map<F>> maps, ReferencedEnvelope bounds) {}