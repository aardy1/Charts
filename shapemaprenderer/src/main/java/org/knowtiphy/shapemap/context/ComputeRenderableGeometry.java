/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.context;

import java.util.ArrayList;
import java.util.List;
import org.knowtiphy.shapemap.api.RenderableGeometry;
import org.knowtiphy.shapemap.api.RenderableShape;
import org.locationtech.jts.geom.Geometry;
import static org.locationtech.jts.geom.Geometry.TYPENAME_LINEARRING;
import static org.locationtech.jts.geom.Geometry.TYPENAME_LINESTRING;
import static org.locationtech.jts.geom.Geometry.TYPENAME_MULTILINESTRING;
import static org.locationtech.jts.geom.Geometry.TYPENAME_MULTIPOINT;
import static org.locationtech.jts.geom.Geometry.TYPENAME_POINT;
import static org.locationtech.jts.geom.Geometry.TYPENAME_POLYGON;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public class ComputeRenderableGeometry {

    public static RenderableGeometry compute(Geometry geometry) {

        //  only missing collections?
        return switch (geometry.getGeometryType()) {
            case TYPENAME_POINT, TYPENAME_LINESTRING, TYPENAME_LINEARRING -> {
                var converted = List.of(convertSimple(geometry));
                yield new RenderableGeometry(converted, converted);
            }
            case TYPENAME_POLYGON -> convertPolygon((Polygon) geometry);
            case TYPENAME_MULTIPOINT -> convertMultiPoint((MultiPoint) geometry);
            case TYPENAME_MULTILINESTRING -> convertMultiLineString((MultiLineString) geometry);
            case Geometry.TYPENAME_MULTIPOLYGON -> convertMultiPolygon((MultiPolygon) geometry);
            default -> null;
        };
    }

    public static RenderableGeometry convertPolygon(Polygon polygon) {

        var simplePoly = RemoveHolesFromPolygon.remove(polygon);
        var converted = convertSimple(simplePoly);

        var boundary = new ArrayList<RenderableShape>(polygon.getNumInteriorRing());
        for (int i = 0; i < simplePoly.getNumInteriorRing(); i++) {
            boundary.add(convertSimple(simplePoly.getInteriorRingN(i)));
        }

        return new RenderableGeometry(List.of(converted), boundary);
    }

    private static RenderableGeometry convertMultiPoint(MultiPoint multiPoint) {

        var converted = new ArrayList<RenderableShape>(multiPoint.getNumGeometries());

        for (var i = 0; i < multiPoint.getNumGeometries(); i++) {
            var point = multiPoint.getGeometryN(i);
            assert point instanceof Point;
            converted.add(convertSimple(point));
        }

        return new RenderableGeometry(converted, converted);
    }

    private static RenderableGeometry convertMultiLineString(MultiLineString multiLine) {

        var converted = new ArrayList<RenderableShape>(multiLine.getNumGeometries());

        for (var i = 0; i < multiLine.getNumGeometries(); i++) {
            var line = multiLine.getGeometryN(i);
            assert line instanceof LineString;
            converted.add(convertSimple(line));
        }

        return new RenderableGeometry(converted, converted);
    }

    private static RenderableGeometry convertMultiPolygon(MultiPolygon multiPolygon) {

        var forFill = new ArrayList<RenderableShape>(multiPolygon.getNumGeometries());
        var forStroke = new ArrayList<RenderableShape>(multiPolygon.getNumGeometries());

        for (var i = 0; i < multiPolygon.getNumGeometries(); i++) {
            var poly = multiPolygon.getGeometryN(i);
            assert poly instanceof Polygon;
            var rPoly = convertPolygon((Polygon) poly);
            forFill.addAll(rPoly.forFill());
            forStroke.addAll(rPoly.forStroke());
        }

        return new RenderableGeometry(forFill, forStroke);
    }

    //  convert a geometry with one sub-geometry -- so a simple line, simple polygon etc
    private static RenderableShape convertSimple(Geometry g) {

        assert g.getNumGeometries() == 1;
        var numPts = g.getNumPoints();
        var xs = new double[numPts];
        var ys = new double[numPts];
        // TODO -- this is potentially a copy. JTS docs have a comment on how to avoid it
        var coords = g.getCoordinates();
        for (var i = 0; i < numPts; i++) {
            xs[i] = coords[i].getX();
            ys[i] = coords[i].getY();
        }

        return new RenderableShape(xs, ys);
    }
}

//    public static Geometry removePolygonG(Polygon polygon) {
//
//        if (polygon.getNumInteriorRing() == 0) {
//            return polygon;
//        }
//
//        // get the holes in the polygon
//        var holes = holes(polygon);
//
//        // copy the boundary of the polygon
//        List<Coordinate> newBoundary = new ArrayList<>();
//        var extRing = polygon.getExteriorRing();
//        for (int i = 0; i < extRing.getNumPoints(); i++) {
//            newBoundary.add(extRing.getCoordinateN(i));
//        }
//
//        // remove each hole in order, building a new polygon boundary each time
//        for (var hole : holes) {
//            newBoundary = removeHole(hole, newBoundary);
//        }
//
//        return GF.createPolygon(GF.createLinearRing(newBoundary.toArray(Coordinate[]::new)));
//
//        //            System.out.println("res = " + result);
//    }