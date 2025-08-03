/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.context;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import org.apache.commons.lang3.tuple.Triple;
import org.girod.javafx.svgimage.SVGLoader;
import org.knowtiphy.shapemap.api.ISVGProvider;

/** A simple cache of SVG images at varying sizes and rotations. */
public class SVGCache implements ISVGProvider {
    private static final SnapshotParameters SVG_RENDERING_PARAMETERS = new SnapshotParameters();

    static {
        SVG_RENDERING_PARAMETERS.setFill(Color.TRANSPARENT);
    }

    private final Class<?> resourceLoader;

    public SVGCache(Class<?> resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private final Map<Triple<String, Integer, Double>, Image> cache = new HashMap<>();

    public void put(String name, int size, double rotation, Image image) {
        cache.put(Triple.of(name, size, rotation), image);
    }

    @Override
    public Image get(String name, int size, double rotation) {
        return cache.computeIfAbsent(
                Triple.of(name, size, rotation),
                key -> {
                    //                            System.out.println("Loading " + name + " " + size
                    // + " : " + rotation);
                    var svgImage = SVGLoader.load(resourceLoader.getResource(name));
                    svgImage.setScaleX(size / svgImage.getWidth());
                    svgImage.setScaleY(size / svgImage.getHeight());
                    // for some reason SVGLoader loads the images upside down ...
                    //  TODO how does this work sharing the rendering parameters?
                    SVG_RENDERING_PARAMETERS.setTransform(new Rotate(180 - rotation));
                    return svgImage.toImage(SVG_RENDERING_PARAMETERS);
                });
    }
}