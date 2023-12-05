package shapemap.renderer;

import java.text.MessageFormat;
import java.util.logging.Logger;
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
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

public final class RendererUtilities {

	private static final Logger LOGGER = org.geotools.util.logging.Logging.getLogger(RendererUtilities.class);

	/**
	 * Helber class for building affine transforms. We use one instance per thread, in
	 * order to avoid the need for {@code synchronized} statements.
	 */
	private static final ThreadLocal<GridToEnvelopeMapper> gridToEnvelopeMappers = new ThreadLocal<GridToEnvelopeMapper>() {
		@Override
		protected GridToEnvelopeMapper initialValue() {
			final GridToEnvelopeMapper mapper = new GridToEnvelopeMapper();
			mapper.setPixelAnchor(PixelInCell.CELL_CORNER);
			return mapper;
		}
	};

	private static double getGeodeticSegmentLength(LineString ls) {
		Coordinate start = ls.getCoordinateN(0);
		Coordinate end = ls.getCoordinateN(1);
		return getGeodeticSegmentLength(start.x, start.y, end.x, end.y);
	}

	private static double getGeodeticSegmentLength(double minx, double miny, double maxx, double maxy) {
		final GeodeticCalculator calculator = new GeodeticCalculator(DefaultGeographicCRS.WGS84);
		double rminx = rollLongitude(minx);
		double rminy = rollLatitude(miny);
		double rmaxx = rollLongitude(maxx);
		double rmaxy = rollLatitude(maxy);
		calculator.setStartingGeographicPoint(rminx, rminy);
		calculator.setDestinationGeographicPoint(rmaxx, rmaxy);
		return calculator.getOrthodromicDistance();
	}

	protected static double rollLongitude(final double x) {
		double rolled = x - (((int) (x + Math.signum(x) * 180)) / 360) * 360.0;
		return rolled;
	}

	protected static double rollLatitude(final double x) {
		double rolled = x - (((int) (x + Math.signum(x) * 90)) / 180) * 180.0;
		return rolled;
	}

	/**
	 * This worldToScreenTransform method makes the assumption that the crs is in Lon,Lat
	 * or Lat,Lon. If the provided envelope does not carry along a crs the assumption that
	 * the map extent is in the classic Lon,Lat form. In case the provided envelope is of
	 * type.
	 *
	 * <p>
	 * Note that this method takes into account also the OGC standard with respect to the
	 * relation between pixels and sample.
	 * @param mapExtent The envelope of the map in lon,lat
	 * @param paintArea The area to paint as a rectangle
	 * @todo add georeferenced envelope check when merge with trunk will be performed
	 */
	public static Affine worldToScreenTransform(Envelope mapExtent, Rectangle2D paintArea,
			CoordinateReferenceSystem destinationCrs) throws TransformException, NonInvertibleTransformException {

		// is the crs also lon,lat?
		final CoordinateReferenceSystem crs2D = CRS.getHorizontalCRS(destinationCrs);
		if (crs2D == null)
			throw new TransformException(
					MessageFormat.format(ErrorKeys.CANT_REDUCE_TO_TWO_DIMENSIONS_$1, destinationCrs));
		final boolean lonFirst = crs2D.getCoordinateSystem().getAxis(0).getDirection().absolute()
				.equals(AxisDirection.EAST);
		final GeneralBounds newEnvelope = lonFirst
				? new GeneralBounds(new double[] { mapExtent.getMinX(), mapExtent.getMinY() },
						new double[] { mapExtent.getMaxX(), mapExtent.getMaxY() })
				: new GeneralBounds(new double[] { mapExtent.getMinY(), mapExtent.getMinX() },
						new double[] { mapExtent.getMaxY(), mapExtent.getMaxX() });
		newEnvelope.setCoordinateReferenceSystem(destinationCrs);

		//
		// with this method I can build a world to grid transform
		// without adding half of a pixel translations. The cost
		// is a hashtable lookup. The benefit is reusing the last
		// transform (instead of creating a new one) if the grid
		// and envelope are the same one than during last invocation.
		final GridToEnvelopeMapper m = gridToEnvelopeMappers.get();
		m.setGridRange(new GridEnvelope2D((int) paintArea.getMinX(), (int) paintArea.getMinY(),
				(int) paintArea.getWidth(), (int) paintArea.getHeight()));
		m.setEnvelope(newEnvelope);
		return m.createTransform().createInverse();
	}

	/**
	 * Finds the centroid of the input geometry if input = point, line, polygon --> return
	 * a point that represents the centroid of that geom if input = geometry collection
	 * --> return a multipoint that represents the centoid of each sub-geom
	 */
	public static Geometry getCentroid(Geometry g) {
		if (g instanceof Point || g instanceof MultiPoint) {
			return g;
		}
		else if (g instanceof GeometryCollection) {
			final GeometryCollection gc = (GeometryCollection) g;
			final Coordinate[] pts = new Coordinate[gc.getNumGeometries()];
			final int length = gc.getNumGeometries();
			for (int t = 0; t < length; t++) {
				pts[t] = pointInGeometry(gc.getGeometryN(t)).getCoordinate();
			}
			return g.getFactory().createMultiPoint(new CoordinateArraySequence(pts));
		}
		else if (g != null) {
			return pointInGeometry(g);
		}
		return null;
	}

	private static Geometry pointInGeometry(Geometry g) {
		Point p = g.getCentroid();
		if (g instanceof Polygon) {
			// if the geometry is heavily generalized centroid computation may fail and
			// return NaN
			if (Double.isNaN(p.getX()) || Double.isNaN(p.getY()))
				return g.getFactory().createPoint(g.getCoordinate());
			// otherwise let's check if the point is inside. Again, this check and
			// "getInteriorPoint"
			// will work only if the geometry is valid
			if (g.isValid() && !g.contains(p)) {
				try {
					p = g.getInteriorPoint();
				}
				catch (Exception e) {
					// generalized geometries might make interior point go bye bye
					return p;
				}
			}
			else {
				return p;
			}
		}
		return p;
	}

	/**
	 * Finds a centroid for a polygon catching any exceptions resulting from
	 * generalization or other polygon irregularities.
	 * @param geom The polygon.
	 * @return The polygon centroid, or null if it can't be found.
	 */
	public static Point getPolygonCentroid(Polygon geom) {
		Point centroid;
		try {
			centroid = geom.getCentroid();
		}
		catch (Exception e) {
			// generalized polygons causes problems - this
			// tries to hide them.
			try {
				centroid = geom.getExteriorRing().getCentroid();
			}
			catch (Exception ee) {
				try {
					centroid = geom.getFactory().createPoint(geom.getCoordinate());
				}
				catch (Exception eee) {
					return null; // we're hooped
				}
			}
		}
		return centroid;
	}

}