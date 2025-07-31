package org.knowtiphy.shapemap.renderer;

import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.cs.AxisDirection;
import org.geotools.api.referencing.datum.PixelInCell;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.GeneralBounds;
import org.geotools.metadata.i18n.ErrorKeys;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Envelope;

import java.text.MessageFormat;

public class RendererUtilities {
    /**
     * Helber class for building affine transforms. We use one instance per thread, in order to
     * avoid the need for {@code synchronized} statements.
     */
    private static final ThreadLocal<GridToEnvelopeMapper> gridToEnvelopeMappers =
            ThreadLocal.withInitial(
                    () -> {
                        final GridToEnvelopeMapper mapper = new GridToEnvelopeMapper();
                        mapper.setPixelAnchor(PixelInCell.CELL_CORNER);
                        return mapper;
                    });

    /**
     * This worldToScreenTransform method makes the assumption that the crs is in Lon,Lat or
     * Lat,Lon. If the provided envelope does not carry along a crs the assumption that the map
     * extent is in the classic Lon,Lat form. In case the provided envelope is of type.
     *
     * <p>Note that this method takes into account also the OGC standard with respect to the
     * relation between pixels and sample.
     *
     * @param mapExtent The envelope of the map in lon,lat
     * @param paintArea The area to paint as a rectangle
     * @todo add georeferenced envelope check when merge with trunk will be performed
     */
    public static Affine worldToScreenTransform(
            Envelope mapExtent, Rectangle2D paintArea, CoordinateReferenceSystem destinationCrs)
            throws TransformException, NonInvertibleTransformException {

        // is the crs also lon,lat?
        final CoordinateReferenceSystem crs2D = CRS.getHorizontalCRS(destinationCrs);
        if (crs2D == null) {
            throw new TransformException(
                    MessageFormat.format(
                            ErrorKeys.CANT_REDUCE_TO_TWO_DIMENSIONS_$1, destinationCrs));
        }
        final boolean lonFirst =
                crs2D.getCoordinateSystem()
                        .getAxis(0)
                        .getDirection()
                        .absolute()
                        .equals(AxisDirection.EAST);
        final GeneralBounds newEnvelope =
                lonFirst
                        ? new GeneralBounds(
                                new double[] {mapExtent.getMinX(), mapExtent.getMinY()},
                                new double[] {mapExtent.getMaxX(), mapExtent.getMaxY()})
                        : new GeneralBounds(
                                new double[] {mapExtent.getMinY(), mapExtent.getMinX()},
                                new double[] {mapExtent.getMaxY(), mapExtent.getMaxX()});
        newEnvelope.setCoordinateReferenceSystem(destinationCrs);

        //
        // with this method I can build a world to grid transform
        // without adding half of a pixel translations. The cost
        // is a hashtable lookup. The benefit is reusing the last
        // transform (instead of creating a new one) if the grid
        // and envelope are the same one than during last invocation.
        final GridToEnvelopeMapper m = gridToEnvelopeMappers.get();
        m.setGridRange(
                new GridEnvelope2D(
                        (int) paintArea.getMinX(),
                        (int) paintArea.getMinY(),
                        (int) paintArea.getWidth(),
                        (int) paintArea.getHeight()));
        m.setEnvelope(newEnvelope);
        return m.createTransform().createInverse();
    }
}