package org.knowtiphy.charts.geotools;

import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.crs.GeographicCRS;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.knowtiphy.shapemap.model.BaseMapViewModel;
import org.knowtiphy.shapemap.model.MapViewModel;
import org.knowtiphy.shapemap.renderer.Transformation;
import si.uom.SI;

import javax.measure.Unit;
import javax.measure.quantity.Length;
import java.text.NumberFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Coordinates
{

  public static double distanceAcross(ReferencedEnvelope referencedEnvelope)
  {
    var degreeDiff = referencedEnvelope.getMaxX() - referencedEnvelope.getMinX();
    return toMeters(degreeDiff, referencedEnvelope.getCoordinateReferenceSystem());
  }

  public static ReferencedEnvelope zoom(ReferencedEnvelope envelope, double zoomFactor)
  {
    var newWidth = envelope.getWidth() * zoomFactor;
    var newHeight = envelope.getHeight() * zoomFactor;
    // expanding mutates the envelope so copy it
    var copy = new ReferencedEnvelope(envelope);
    copy.expandBy((newWidth - envelope.getWidth()) / 2, (newHeight - envelope.getHeight()) / 2);
    return copy;
//    clip(maxExtent, copy, crs);
  }

  public static <S, F> void zoom(BaseMapViewModel<S, F> map, double zoomFactor)
  {
    try
    {
      var vpBounds = map.viewPortBounds();
      var newExtent = zoom(vpBounds, zoomFactor);
      if(!newExtent.equals(vpBounds))
      {
        map.setViewPortBounds(newExtent);
      }
    }
    catch(TransformException | NonInvertibleTransformException ex)
    {
      Logger.getLogger(Coordinates.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private static final double DEFAULT_WIDTH = 3;

  private static final double DEFAULT_HEIGHT = 3;

  // need to make this adaptive -- like 1/2 the maxX
  public static <S, F> void positionAt(BaseMapViewModel<S, F> map, double x, double y)
    throws NonInvertibleTransformException, TransformException
  {

    var world = map.viewPortBounds();

    var defaultWidth = world.getWidth() <= 2 * DEFAULT_WIDTH ? world.getWidth() / 8 : DEFAULT_WIDTH;
    var defaultHeight = world.getHeight() <= 2 * DEFAULT_HEIGHT ? world.getHeight() / 8 :
                          DEFAULT_HEIGHT;

    // TODO -- why does this not center the view port on the x, y world coords?
    var tx = new Transformation(map.viewPortScreenToWorld());
    tx.apply(x, y);

    var envelope = new ReferencedEnvelope(tx.getX() - defaultWidth, tx.getX() + defaultWidth,
      tx.getY() - defaultHeight, tx.getY() + defaultHeight, map.crs());
//    var newExtent = clip(map, envelope);
    map.setViewPortBounds(envelope);
  }

//  public static <S, F> ReferencedEnvelope clip(
//    BaseMapViewModel<S, F> map, ReferencedEnvelope envelope)
//  {
//    return clip(map.viewPortBounds(), envelope, map.crs());
//  }

//  public static ReferencedEnvelope clip(
//    ReferencedEnvelope maxExtent, ReferencedEnvelope envelope, CoordinateReferenceSystem crs)
//  {
//    if(true)
//    {
//      return envelope;
//    }
//    var width = Math.min(envelope.getWidth(), maxExtent.getWidth());
//    var height = Math.min(envelope.getHeight(), maxExtent.getHeight());
//
//    var minX = envelope.getMinX();
//    var maxX = envelope.getMaxX();
//    var minY = envelope.getMinY();
//    var maxY = envelope.getMaxY();
//
//    if(maxX > maxExtent.getMaxX())
//    {
//      maxX = maxExtent.getMaxX();
//      minX = Math.max(maxExtent.getMinX(), maxX - width);
//    }
//    else if(minX < maxExtent.getMinX())
//    {
//      minX = maxExtent.getMinX();
//      maxX = Math.min(maxExtent.getMaxX(), minX + width);
//    }
//
//    if(maxY > maxExtent.getMaxY())
//    {
//      maxY = maxExtent.getMaxY();
//      minY = Math.max(maxExtent.getMinY(), maxY - height);
//    }
//    else if(minY < maxExtent.getMinY())
//    {
//      minY = maxExtent.getMinY();
//      maxY = Math.min(maxExtent.getMaxY(), minY + height);
//    }
//
//    assert width >= 0;
//    assert width <= maxExtent.getWidth();
//    assert height >= 0;
//    assert height <= maxExtent.getHeight();
//
//    assert minX <= maxExtent.getMaxX() : minX + "::" + maxExtent.getMaxX();
//    assert minX >= maxExtent.getMinX() : minX;
//    assert maxX <= maxExtent.getMaxX() : maxX;
//    assert maxX >= maxExtent.getMinX() : maxX;
//
//    assert minY <= maxExtent.getMaxY() : minY + "::" + maxExtent.getMaxY();
//    assert minY >= maxExtent.getMinY() : minY + "::" + maxExtent.getMinY();
//    assert maxY <= maxExtent.getMaxY() : maxY + "::" + maxExtent.getMaxY();
//    assert maxY >= maxExtent.getMinY() : maxY + "::" + maxExtent.getMaxY();
//
//    return new ReferencedEnvelope(minX, maxX, minY, maxY, crs);
//  }

  public static <S, F> double getMapScale(MapViewModel<S, F> map, int screenWidth, double dpi)
  {
    // if it's geodetic, we're dealing with lat/lon unit measures
    var crs = map.viewPortBounds().getCoordinateReferenceSystem();
    double width = map.viewPortBounds().getWidth();
    double widthMeters = toMeters(width, crs);
    return widthMeters / (screenWidth / getDpi(Map.of("dpi", dpi)) * 0.0254);
  }

  /**
   * Either gets a DPI from the hints, or return the OGC standard, stating that a pixel
   * is 0.28 mm (the result is a non integer DPI...)
   *
   * @return DPI as doubles, to avoid issues with integer trunking in scale computation
   * expression
   */
  public static double getDpi(Map<String, Object> hints)
  {
    if(hints != null && hints.containsKey("dpi"))
    {
      return ((Number) hints.get("dpi")).doubleValue();
    }
    else
    {
      return 25.4 / 0.28; // 90 = OGC standard
    }
  }

  static final double OGC_DEGREE_TO_METERS = 6378137.0 * 2.0 * Math.PI / 360;

  private static final Logger LOGGER = org.geotools.util.logging.Logging.getLogger(
    Coordinates.class);

  /**
   * Enable unit correction in {@link #toMeters(double, CoordinateReferenceSystem)}
   * calculation.
   *
   * <p>
   * Toggle for a bug fix that will invalidate a good number of SLDs out there (and
   * thus, we allow people to turn off the fix).
   */
  static boolean SCALE_UNIT_COMPENSATION = Boolean.parseBoolean(
    System.getProperty("org.geotoools.render.lite.scale.unitCompensation", "true"));

  /**
   * Method used by the OGC scale calculation to turn a given length in the specified
   * CRS towards meters.
   *
   * <p>
   * GeographicCRS uses {@link #OGC_DEGREE_TO_METERS} for conversion of lat/lon measures
   *
   * <p>
   * Otherwise the horizontal component of the CRS is assumed to have a uniform axis
   * unit of measure providing the Unit used for conversion. To ignore unit disable
   * {@link #SCALE_UNIT_COMPENSATION} to for the unaltered size.
   *
   * @return size adjusted for GeographicCRS or CRS units
   */
  public static double toMeters(double size, CoordinateReferenceSystem crs)
  {
    if(crs == null)
    {
      LOGGER.finer("toMeters: assuming the original size is in meters already, as crs is null");
      return size;
    }
    if(crs instanceof GeographicCRS)
    {
      return size * OGC_DEGREE_TO_METERS;
    }
    if(!SCALE_UNIT_COMPENSATION)
    {
      return size;
    }
    CoordinateReferenceSystem horizontal = CRS.getHorizontalCRS(crs);
    if(horizontal != null)
    {
      crs = horizontal;
    }
    @SuppressWarnings("unchecked") Unit<Length> unit = (Unit<Length>) crs
                                                                        .getCoordinateSystem()
                                                                        .getAxis(0)
                                                                        .getUnit();
    if(unit == null)
    {
      LOGGER.finer(
        "toMeters: assuming the original size is in meters already, as the first crs axis unit " + "is" + " null. CRS is " + crs);
      return size;
    }
    if(!unit.isCompatible(SI.METRE))
    {
      LOGGER.warning("toMeters: could not convert unit " + unit + " to meters");
      return size;
    }
    return unit.getConverterTo(SI.METRE).convert(size);
  }

  private static final NumberFormat TWO_PLACES = NumberFormat.getNumberInstance();

  static
  {
    TWO_PLACES.setMaximumFractionDigits(2);
    TWO_PLACES.setMinimumFractionDigits(2);
  }

  public static String twoDec(double value)
  {
    return TWO_PLACES.format(Math.round(value));
  }

}