/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer;

import javafx.scene.transform.Affine;
import org.geotools.api.coverage.grid.GridEnvelope;
import org.geotools.api.geometry.Bounds;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.cs.AxisDirection;
import org.geotools.api.referencing.cs.CoordinateSystem;
import org.geotools.api.referencing.datum.PixelInCell;
import org.geotools.referencing.operation.LinearTransform;
import org.geotools.referencing.operation.matrix.MatrixFactory;
import org.geotools.referencing.operation.transform.ProjectiveTransform;

/**
 * A helper class for building <var>n</var>-dimensional {@linkplain Affine affine transform} mapping
 * {@linkplain GridEnvelope grid ranges} to {@linkplain Bounds envelopes}. The affine transform will
 * be computed automatically from the information specified by the {@link #setGridRange
 * setGridRange} and {@link #setEnvelope setEnvelope} methods, which are mandatory. All other setter
 * methods are optional hints about the affine transform to be created. This builder is convenient
 * when the following conditions are meet:
 *
 * <p>
 *
 * <ul>
 *   <li>
 *       <p>Pixels coordinates (usually (<var>x</var>,<var>y</var>) integer values inside the
 *       rectangle specified by the grid range) are expressed in some {@linkplain
 *       CoordinateReferenceSystem coordinate reference system} known at compile time. This is often
 *       the case. For example the CRS attached to {@link BufferedImage} has always ({@linkplain
 *       AxisDirection#COLUMN_POSITIVE column}, {@linkplain AxisDirection#ROW_POSITIVE row}) axis,
 *       with the origin (0,0) in the upper left corner, and row values increasing down.
 *   <li>
 *       <p>"Real world" coordinates (inside the envelope) are expressed in arbitrary
 *       <em>horizontal</em> coordinate reference system. Axis directions may be ({@linkplain
 *       AxisDirection#NORTH North}, {@linkplain AxisDirection#WEST West}), or ({@linkplain
 *       AxisDirection#EAST East}, {@linkplain AxisDirection#NORTH North}), <cite>etc.</cite>.
 * </ul>
 *
 * <p>In such case (and assuming that the image's CRS has the same characteristics than the {@link
 * BufferedImage}'s CRS described above):
 *
 * <p>
 *
 * <ul>
 *   <li>
 *       <p>{@link #setSwapXY swapXY} shall be set to {@code true} if the "real world" axis order is
 *       ({@linkplain AxisDirection#NORTH North}, {@linkplain AxisDirection#EAST East}) instead of
 *       ({@linkplain AxisDirection#EAST East}, {@linkplain AxisDirection#NORTH North}). This axis
 *       swapping is necessary for mapping the ({@linkplain AxisDirection#COLUMN_POSITIVE column},
 *       {@linkplain AxisDirection#ROW_POSITIVE row}) axis order associated to the image CRS.
 *   <li>
 *       <p>In addition, the "real world" axis directions shall be reversed (by invoking <code>
 *       {@linkplain #reverseAxis reverseAxis}(dimension)</code>) if their direction is {@link
 *       AxisDirection#WEST WEST} (<var>x</var> axis) or {@link AxisDirection#NORTH NORTH}
 *       (<var>y</var> axis), in order to get them oriented toward the {@link AxisDirection#EAST
 *       EAST} or {@link AxisDirection#SOUTH SOUTH} direction respectively. The later may seems
 *       unatural, but it reflects the fact that row values are increasing down in an {@link
 *       BufferedImage}'s CRS.
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @version $Id$
 * @since 2.3
 */
public class GridToEnvelopeMapper {
    //  bit mask for the {@link #setSwapXY swapXY} property.
    private static final int SWAP_XY = 1;

    //  bit mask for the {@link #setReverseAxis reverseAxis} property.
    private static final int REVERSE_AXIS = 2;

    //  combination of bit masks telling which property were user-defined.
    private int defined;

    //  the grid range
    private GridEnvelope gridRange;

    //  the envelope
    private Bounds envelope;

    /**
     * Whatever the {@code gridToCRS} transform will maps pixel center or corner. The default value
     * is {@link PixelInCell#CELL_CENTER}.
     */
    private PixelInCell anchor = PixelInCell.CELL_CENTER;

    /**
     * {@code true} if we should swap the two first axis, {@code false} if we should not swap and
     * {@code null} if this state is not yet determined.
     */
    private Boolean swapXY;

    /** The axis to reverse, or {@code null} if none or not yet determined. */
    private boolean[] reverseAxis;

    /**
     * Sets whatever the grid range maps {@linkplain PixelInCell#CELL_CENTER pixel center} or
     * {@linkplain PixelInCell#CELL_CORNER pixel corner}. The former is OGC convention while the
     * later is Java2D/JAI convention.
     *
     * @param anchor Whatever the grid range maps pixel center or corner.
     * @since 2.5
     */
    public void setPixelAnchor(PixelInCell anchor) {
        this.anchor = anchor;
        reset();
    }

    /**
     * Sets the grid range.
     *
     * @param newGridRange The new grid range.
     */
    public void setGridRange(GridEnvelope newGridRange) {
        this.gridRange = newGridRange;
        reset();
    }

    /**
     * Returns the envelope.
     *
     * @return The envelope.
     */
    public Bounds envelope() {
        return envelope;
    }

    /**
     * Sets the envelope.
     *
     * @param newEnvelope The new envelope.
     */
    public void setEnvelope(Bounds newEnvelope) {
        this.envelope = newEnvelope;
        reset();
    }

    /**
     * Returns {@code true} if the two first axis should be interchanged. If <code>
     * {@linkplain #isAutomatic isAutomatic}({@linkplain #SWAP_XY})</code> returns {@code true}
     * (which is the default), then this method make the following assumptions:
     *
     * <ul>
     *   <li>
     *       <p>Axis order in the grid range matches exactly axis order in the envelope, except for
     *       the special case described in the next point. In other words, if axis order in the
     *       underlying image is (<var>column</var>, <var>row</var>) (which is the case for a
     *       majority of images), then the envelope should probably have a (<var>longitude</var>,
     *       <var>latitude</var>) or (<var>easting</var>, <var>northing</var>) axis order.
     *   <li>
     *       <p>An exception to the above rule applies for CRS using exactly the following axis
     *       order: ({@link AxisDirection#NORTH NORTH}|{@link AxisDirection#SOUTH SOUTH}, {@link
     *       AxisDirection#EAST EAST}|{@link AxisDirection#WEST WEST}). An example of such CRS is
     *       {@code EPSG:4326}. In this particular case, this method will returns {@code true}, thus
     *       suggesting to interchange the (<var>y</var>,<var>x</var>) axis for such CRS.
     * </ul>
     *
     * @return {@code true} if the two first axis should be interchanged.
     */
    public boolean getSwapXY() {
        if (swapXY == null) {
            var value = false;
            if (isAutomatic(SWAP_XY)) {
                value = swapXY(getCoordinateSystem());
            }
            swapXY = value;
        }

        return swapXY;
    }

    /**
     * Returns which (if any) axis in <cite>user</cite> space (not grid space) should have their
     * direction reversed. If <code>
     * {@linkplain #isAutomatic isAutomatic}({@linkplain #REVERSE_AXIS})</code> returns {@code true}
     * (which is the default), then this method make the following assumptions:
     *
     * <p>
     *
     * <ul>
     *   <li>Axis should be reverted if needed in order to point toward their "{@linkplain
     *       AxisDirection#absolute absolute}" direction.
     *   <li>An exception to the above rule is the second axis in grid space, which is assumed to be
     *       the <var>y</var> axis on output device (usually the screen). This axis is reversed
     *       again in order to match the bottom direction often used with such devices.
     * </ul>
     *
     * @return The reversal state of each axis, or {@code null} if unspecified.
     */
    public boolean[] getReverseAxis() {
        if (reverseAxis == null) {
            var cs = getCoordinateSystem();
            var dimension = cs.getDimension();
            reverseAxis = new boolean[dimension];
            if (isAutomatic(REVERSE_AXIS)) {
                for (var i = 0; i < dimension; i++) {
                    var direction = cs.getAxis(i).getDirection();
                    var absolute = direction.absolute();
                    reverseAxis[i] =
                            direction != AxisDirection.OTHER
                                    && direction.equals(absolute.opposite());
                }
                if (dimension >= 2) {
                    var i = getSwapXY() ? 0 : 1;
                    reverseAxis[i] = !reverseAxis[i];
                }
            }
        } else {
            // No coordinate system. Reverse the second axis inconditionnaly
            // (except if there is not enough dimensions).
            var length = 0;
            if (gridRange != null) {
                length = gridRange.getDimension();
            } else if (envelope != null) {
                length = envelope.getDimension();
            }
            if (length >= 2) {
                reverseAxis = new boolean[length];
                reverseAxis[1] = true;
            }
        }

        return reverseAxis;
    }

    /**
     * Returns {@code true} if all properties designed by the specified bit mask will be computed
     * automatically.
     *
     * @param mask Any combination of {@link #REVERSE_AXIS} or {@link #SWAP_XY}.
     * @return {@code true} if all properties given by the mask will be computed automatically.
     */
    public boolean isAutomatic(int mask) {
        return (defined & mask) == 0;
    }

    /**
     * Creates an affine transform using the information provided by setter methods.
     *
     * @return The transform.
     * @throws IllegalStateException if the grid range or the envelope were not set.
     */
    public Affine createTransform() {
        var swapXandY = getSwapXY();
        var reverse = getReverseAxis();

        /*
         * Setup the multi-dimensional affine transform for use with OpenGIS. According
         * OpenGIS specification, transforms must map pixel center. This is done by adding
         * 0.5 to grid coordinates.
         */

        double translate = PixelInCell.CELL_CENTER.equals(anchor) ? 0.5 : 0;

        var matrix = MatrixFactory.create(gridRange.getDimension() + 1);
        for (var i = 0; i < gridRange.getDimension(); i++) {
            // NOTE: i is a dimension in the 'gridRange' space (source coordinates).
            // j is a dimension in the 'userRange' space (target coordinates).
            var j = i;
            if (swapXandY && j <= 1) {
                j = 1 - j;
            }
            var scale = envelope.getSpan(j) / gridRange.getSpan(i);
            double offset;
            if (reverse == null || j >= reverse.length || !reverse[j]) {
                offset = envelope.getMinimum(j);
            } else {
                scale = -scale;
                offset = envelope.getMaximum(j);
            }

            offset -= scale * (gridRange.getLow(i) - translate);
            matrix.setElement(j, j, 0.0);
            matrix.setElement(j, i, scale);
            matrix.setElement(j, gridRange.getDimension(), offset);
        }

        return createAffine(ProjectiveTransform.create(matrix));
    }

    /** Flush any cached values */
    private void reset() {
        if (isAutomatic(REVERSE_AXIS)) {
            reverseAxis = null;
        }
        if (isAutomatic(SWAP_XY)) {
            swapXY = null;
        }
    }

    /** Returns the coordinate system of the envelope. */
    private CoordinateSystem getCoordinateSystem() {
        return envelope.getCoordinateReferenceSystem().getCoordinateSystem();
    }

    /**
     * Applies heuristic rules in order to determine if the two first axis should be interchanged.
     */
    private static boolean swapXY(CoordinateSystem cs) {
        if (cs != null && cs.getDimension() >= 2) {
            return AxisDirection.NORTH.equals(cs.getAxis(0).getDirection().absolute())
                    && AxisDirection.EAST.equals(cs.getAxis(1).getDirection().absolute());
        }

        return false;
    }

    // Geotools
    // [ x'] [ m00 m01 m02 ] [ x ]
    // [ y'] = [ m10 m11 m12 ] [ y ]
    // [ 1 ] [ m20 m21 1 ] [ 1 ]
    // x' = m * x
    //
    // Javafx
    // [ x'] [ mxx mxy mxz tx ] [ x ] [ mxx * x + mxy * y + mxz * z + tx ]
    // [ y'] = [ myx myy myz ty ] [ y ] = [ myx * x + myy * y + myz * z + ty ]
    // [ z'] [ mzx mzy mzz tz ] [ z ] [ mzx * x + mzy * y + mzz * z + tz ]
    // [ 1 ]

    private static Affine createAffine(LinearTransform tx) {
        var gMatrix = tx.getMatrix();
        //  @formatter:off
        return new Affine(
                gMatrix.getElement(0, 0),
                gMatrix.getElement(0, 1),
                gMatrix.getElement(0, 2),
                gMatrix.getElement(1, 0),
                gMatrix.getElement(1, 1),
                gMatrix.getElement(1, 2));
        //  @formatter:om
    }
}