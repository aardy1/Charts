package org.knowtiphy.charts.geotools;

import java.text.NumberFormat;
import java.util.List;
import java.util.logging.Logger;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.crs.GeographicCRS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.knowtiphy.charts.map.Map;
import org.knowtiphy.charts.platform.IUnderlyingPlatform;
import si.uom.SI;

/** */
public class Coordinates {

    public static double distanceAcross(ReferencedEnvelope referencedEnvelope) {
        var degreeDiff = referencedEnvelope.getMaxX() - referencedEnvelope.getMinX();
        return toMeters(degreeDiff, referencedEnvelope.getCoordinateReferenceSystem());
    }

    public static double distanceDown(ReferencedEnvelope referencedEnvelope) {
        var degreeDiff = referencedEnvelope.getMaxY() - referencedEnvelope.getMinY();
        return toMeters(degreeDiff, referencedEnvelope.getCoordinateReferenceSystem());
    }

    //  compute a bounding box for a collection of maps.

    public static <S, F> ReferencedEnvelope bounds(List<Map<S, F>> maps) {

        var minX = Double.POSITIVE_INFINITY;
        var minY = Double.POSITIVE_INFINITY;
        var maxX = Double.NEGATIVE_INFINITY;
        var maxY = Double.NEGATIVE_INFINITY;

        for (var map : maps) {
            var b = map.bounds();
            minX = Math.min(minX, b.getMinX());
            minY = Math.min(minY, b.getMinY());
            maxX = Math.max(maxX, b.getMaxX());
            maxY = Math.max(maxY, b.getMaxY());
        }

        // TODO -- get the CRS from the cell file
        return new ReferencedEnvelope(minX, maxX, minY, maxY, DefaultGeographicCRS.WGS84);
    }

    static final double OGC_DEGREE_TO_METERS = 6378137.0 * 2.0 * Math.PI / 360;

    private static final Logger LOGGER =
            org.geotools.util.logging.Logging.getLogger(Coordinates.class);

    /**
     * Enable unit correction in {@link #toMeters(double, CoordinateReferenceSystem)} calculation.
     *
     * <p>Toggle for a bug fix that will invalidate a good number of SLDs out there (and thus, we
     * allow people to turn off the fix).
     */
    static boolean SCALE_UNIT_COMPENSATION =
            Boolean.parseBoolean(
                    System.getProperty("org.geotoools.render.lite.scale.unitCompensation", "true"));

    /**
     * Method used by the OGC scale calculation to turn a given length in the specified CRS towards
     * meters.
     *
     * <p>GeographicCRS uses {@link #OGC_DEGREE_TO_METERS} for conversion of lat/lon measures
     *
     * <p>Otherwise the horizontal component of the CRS is assumed to have a uniform axis unit of
     * measure providing the Unit used for conversion. To ignore unit disable {@link
     * #SCALE_UNIT_COMPENSATION} to for the unaltered size.
     *
     * @return size adjusted for GeographicCRS or CRS units
     */
    public static double toMeters(double size, CoordinateReferenceSystem crs) {
        if (crs == null) {
            LOGGER.finer(
                    "toMeters: assuming the original size is in meters already, as crs is null");
            return size;
        }
        if (crs instanceof GeographicCRS) {
            return size * OGC_DEGREE_TO_METERS;
        }
        if (!SCALE_UNIT_COMPENSATION) {
            return size;
        }
        CoordinateReferenceSystem horizontal = CRS.getHorizontalCRS(crs);
        if (horizontal != null) {
            crs = horizontal;
        }
        @SuppressWarnings("unchecked")
        Unit<Length> unit = (Unit<Length>) crs.getCoordinateSystem().getAxis(0).getUnit();
        if (unit == null) {
            LOGGER.finer(
                    "toMeters: assuming the original size is in meters already, as the first crs axis unit "
                            + "is"
                            + " null. CRS is "
                            + crs);
            return size;
        }
        if (!unit.isCompatible(SI.METRE)) {
            LOGGER.warning("toMeters: could not convert unit " + unit + " to meters");
            return size;
        }
        return unit.getConverterTo(SI.METRE).convert(size);
    }

    public static double toCM(double size) {
        return size * 100;
    }

    public static double dScale(
            ReferencedEnvelope bounds, double screenAreaWidthPX, IUnderlyingPlatform platform) {
        var acrossKM = Coordinates.distanceAcross(bounds);
        //        var screenAreaWidthPX = getScreenArea().getWidth();
        var screenAreaWidthCM = platform.windowWidthCM(screenAreaWidthPX);
        //        System.out.println("dist across M = " + acrossKM);
        //        System.out.println("screenAreaWidthPx = " + screenAreaWidthPX);
        //        System.out.println("screenAreaWidthCM = " + screenAreaWidthCM);
        //        System.out.println("dscale width = " + toCM(acrossKM / screenAreaWidthCM));

        //        var downKM = Coordinates.distanceDown(bounds());
        //        var screenAreaHeightPX = screenArea().getHeight();
        //        var screenAreaHeightCM = platform.windowHeightCM(screenAreaHeightPX);
        //        System.out.println("dist down M = " + downKM);
        //        System.out.println("screenAreaHeightPx = " + screenAreaHeightPX);
        //        System.out.println("screenAreaHeightCM = " + screenAreaHeightCM);
        //        System.out.println("dscale height = " + toCM(downKM / screenAreaHeightCM));

        return toCM(acrossKM / screenAreaWidthCM);
    }

    private static final NumberFormat TWO_PLACES = NumberFormat.getNumberInstance();

    static {
        TWO_PLACES.setMaximumFractionDigits(2);
        TWO_PLACES.setMinimumFractionDigits(2);
    }

    public static String twoDec(double value) {
        return TWO_PLACES.format(Math.round(value));
    }
}

    //    private static final double DEFAULT_WIDTH = 3;
    //
    //    private static final double DEFAULT_HEIGHT = 3;

    //      TODO -- keep this as its needed when not quilting.
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

    //    public static <S, F> double getMapScale(MapViewModel<S, F> map, int screenWidth, double
    // dpi) {
    //        // if it's geodetic, we're dealing with lat/lon unit measures
    //        var crs = map.viewPortBounds().getCoordinateReferenceSystem();
    //        double width = map.viewPortBounds().getWidth();
    //        double widthMeters = toMeters(width, crs);
    //        return widthMeters / (screenWidth / getDpi(Map.of("dpi", dpi)) * 0.0254);
    //    }

    //    public static ReferencedEnvelope zoom(ReferencedEnvelope envelope, double zoomFactor) {
    //        var newWidth = envelope.getWidth() * zoomFactor;
    //        var newHeight = envelope.getHeight() * zoomFactor;
    //        // expanding mutates the envelope so copy it
    //        var copy = new ReferencedEnvelope(envelope);
    //        copy.expandBy((newWidth - envelope.getWidth()) / 2, (newHeight - envelope.getHeight())
    // / 2);
    //        return copy;
    //        //    clip(maxExtent, copy, crs);
    //    }