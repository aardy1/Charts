/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.context;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import org.apache.commons.lang3.tuple.Triple;
import org.girod.javafx.svgimage.SVGLoader;
import org.knowtiphy.shapemap.api.ISVGProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author graham
 */
public class SVGCache implements ISVGProvider
{

  private static final SnapshotParameters SVG_RENDERING_PARAMETERS = new SnapshotParameters();

  static
  {
    SVG_RENDERING_PARAMETERS.setFill(Color.TRANSPARENT);
  }

  private final Class<?> resourceLoader;

  public SVGCache(Class<?> resourceLoader)
  {
    this.resourceLoader = resourceLoader;
  }

  //@formatter:on
  private final Map<Triple<String, Integer, Double>, Image> cache = new HashMap<>();

  public void cache(String name, int size, double rotation, Image image)
  {
    cache.put(Triple.of(name, size, rotation), image);
  }

  @Override
  public Image get(String name, int size, double rotation)
  {
    var image = cache.get(Triple.of(name, size, rotation));
    if(image == null)
    {
      var svgImage = SVGLoader.load(resourceLoader.getResource(name));
      svgImage.setScaleX(size / svgImage.getWidth());
      svgImage.setScaleY(size / svgImage.getHeight());
      // for some reason SVGLoader loads the images upside down ...
      SVG_RENDERING_PARAMETERS.setTransform(new Rotate(180 - rotation));
      image = svgImage.toImage(SVG_RENDERING_PARAMETERS);
      cache(name, size, rotation, image);
    }

    return image;
  }

}