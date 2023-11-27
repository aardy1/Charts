/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import java.nio.file.Path;
import java.util.Collection;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Geometry;

/**
 * @author graham
 */
public class ChartDescription {

	private final ENCCell cell;

	private final Path dir;

	public ChartDescription(Path dir, ENCCell cell) {
		this.cell = cell;
		this.dir = dir;
	}

	public String getName() {
		return cell.getLname();
	}

	public int cScale() {
		return cell.getcScale();
	}

	public ENCCell getCell() {
		return cell;
	}

	public boolean intersects(Geometry envelope) {
		return cell.intersects(envelope);
	}

	public Collection<Panel> getPanels() {
		return cell.getPanels();
	}

	public ReferencedEnvelope getBounds(CoordinateReferenceSystem crs) {
		return cell.getBounds(crs);
	}

	public Path getDir() {
		return dir;
	}

}
