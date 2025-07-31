package org.knowtiphy.shapemap.renderer;

import org.geotools.api.coverage.grid.GridEnvelope;
import org.geotools.coverage.grid.GeneralGridEnvelope;
import org.geotools.coverage.grid.GridCoordinates2D;

/**
 * Defines a range of two-dimensional grid coverage coordinates. This implementation extends {@link
 * Rectangle} for interoperability with Java2D. Note that at the opposite of {@link
 * GeneralGridEnvelope}, this class is mutable.
 *
 * <p><b>CAUTION:</b> ISO 19123 defines {@linkplain #getHigh high} coordinates as
 * <strong>inclusive</strong>. We follow this specification for all getters methods, but keep in
 * mind that this is the opposite of Java2D usage where {@link Rectangle} maximal values are
 * exclusive.
 *
 * @since 2.5
 * @version $Id$
 * @author Martin Desruisseaux
 * @see GeneralGridEnvelope
 */
public class GridEnvelope2D implements GridEnvelope {

    private final int width;

    private final int height;

    private final int x;

    private final int y;

    public GridEnvelope2D(final int x, final int y, final int width, final int height) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    @Override
    public final int getDimension() {
        return 2;
    }

    @Override
    public GridCoordinates2D getLow() {
        return new GridCoordinates2D(x, y);
    }

    @Override
    public GridCoordinates2D getHigh() {
        return new GridCoordinates2D(x + width - 1, y + height - 1);
    }

    @Override
    public int getLow(final int dimension) {
        return switch (dimension) {
            case 0 -> x;
            case 1 -> y;
            default -> throw new IndexOutOfBoundsException(dimension);
        };
    }

    @Override
    public int getHigh(final int dimension) {
        return switch (dimension) {
            case 0 -> x + width - 1;
            case 1 -> y + height - 1;
            default -> throw new IndexOutOfBoundsException(dimension);
        };
    }

    @Override
    public int getSpan(final int dimension) {
        return switch (dimension) {
            case 0 -> width;
            case 1 -> height;
            default -> throw new IndexOutOfBoundsException(dimension);
        };
    }
}