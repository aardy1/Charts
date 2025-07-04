/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.shapemap.renderer.context;

import org.apache.commons.lang3.tuple.Pair;
import org.knowtiphy.shapemap.api.IRenderablePolygonProvider;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RemoveHolesFromPolygon implements IRenderablePolygonProvider
{
    private static final GeometryFactory GF = new GeometryFactory();

    private final RenderGeomCache renderGeomCache;

    public RemoveHolesFromPolygon(RenderGeomCache renderGeomCache)
    {
        this.renderGeomCache = renderGeomCache;
    }

    @Override
    public Polygon apply(Polygon polygon)
    {
        return renderGeomCache.computeIfAbsent(polygon, poly -> remove(polygon));
    }

    private Polygon remove(Polygon polygon)
    {
        if(polygon.getNumInteriorRing() == 0)
        {
            return polygon;
        }
        else
        {
            // get the holes in the polygon
            var holes = holes(polygon);

            // copy the boundary of the polygon
            List<Coordinate> newBoundary = new ArrayList<>();
            var extRing = polygon.getExteriorRing();
            for(int i = 0; i < extRing.getNumPoints(); i++)
            {
                newBoundary.add(extRing.getCoordinateN(i));
            }

            // remove each hole in order, building a new polygon boundary each time
            for(var hole : holes)
            {
                newBoundary = removeHole(hole, newBoundary);
            }

            return GF.createPolygon(GF.createLinearRing(newBoundary.toArray(Coordinate[]::new)));
        }
    }

    /**
     * Get a list of pairs of a hole, and the "top most" coordinate in the hole, ordered
     * by the y part of that coordinate (top most y-coordinate first)
     *
     * @param polygon the polygon
     * @return the list of pairs of a hole and the "top most" coordinate
     */

    private List<Pair<LinearRing, Integer>> holes(Polygon polygon)
    {

        var map = new HashMap<LinearRing, Integer>();
        var result = new ArrayList<LinearRing>();

        for(var i = 0; i < polygon.getNumInteriorRing(); i++)
        {
            var ring = polygon.getInteriorRingN(i);
            result.add(ring);
            var minPos = -1;
            for(var j = 0; j < ring.getNumPoints(); j++)
            {
                if(minPos == -1 || northOf(ring.getCoordinateN(j), ring.getCoordinateN(minPos)))
                {
                    minPos = j;
                }
            }

            map.put(ring, minPos);
        }

        result.sort(
            (h1, h2) -> compareY(h1.getCoordinateN(map.get(h1)), h2.getCoordinateN(map.get(h2))));
        return result.stream().map(ring -> Pair.of(ring, map.get(ring))).toList();
    }

    private int compareY(Coordinate a, Coordinate b)
    {
        return -Double.compare(a.y, b.y);
    }

    private boolean northOf(Coordinate a, Coordinate b)
    {
        return compareY(a, b) < 0;
    }

    private List<Coordinate> removeHole(Pair<LinearRing, Integer> hole, List<Coordinate> polygon)
    {

        var ring = hole.getLeft();
        var holeTop = ring.getCoordinateN(hole.getRight());

        // find the (v_b, v_(b_1)) segment on the boundary "directly above" the top most
        // point of the hole

        var b = boundaryIntersection(holeTop, polygon);
        var vb = polygon.get(b);

        var newBoundary = new ArrayList<Coordinate>();

        // copy v_i (i <= b) from the old boundary to the new boundary
        for(int i = 0; i <= b; i++)
        {
            newBoundary.add(polygon.get(i));
        }

        // the point at which we must add the bridge to the hole boundary
        Coordinate bridgePt;

        // if the intersection point is at v_b
        if(vb.x == holeTop.x)
        {
            bridgePt = vb;
        }
        // else, the intersection point is somewhere on (v_b, v_(b+1))
        else
        {
            // TODO -- is this correct if we use long/lat?
            var vb1 = polygon.get(b + 1);
            var slope = (vb1.y - vb.y) / (vb1.x - vb.x);
            var bridgeY = slope * (holeTop.x - vb.x) + vb.y;
            bridgePt = new Coordinate(holeTop.x, bridgeY);

            // add (v_b, bridgePt) to the new boundary
            newBoundary.add(bridgePt);
        }

        // add the hole boundary to the new boundary, maintaining counter-clockwise
        // orientation of the hole
        for(int i = hole.getRight(); i < ring.getNumPoints() - 1; i++)
        {
            newBoundary.add(ring.getCoordinateN(i));
        }

        for(int i = 0; i < hole.getRight(); i++)
        {
            newBoundary.add(ring.getCoordinateN(i));
        }

        newBoundary.add(ring.getCoordinateN(hole.getRight()));

        // close the bridge back to to the old boundary
        newBoundary.add(bridgePt);

        // add the remainder of the old boundary to the new boundary
        for(int i = b + 1; i < polygon.size(); i++)
        {
            newBoundary.add(polygon.get(i));
        }

        return newBoundary;
    }

    /**
     * Given a coordinate point, p, and a polygon, P, find where the line, L, drawn
     * directly upwards from c.y "first" intersects the boundary of P.
     *
     * @param pt   the coordinate point p
     * @param poly the polygon P
     * @return the index, i, of a segment (v_i, v_(i+1)) of the boundary of P, st:
     * <p>
     * a) v_i <= c.x < v_(i+1)
     * <p>
     * b) the line drawn directly upwards from c.y intersects (v_i, v_(i+1))
     * <p>
     * c) the y-coordinate of the intersection point on (v_i, v_(i+1)) is minimal among
     * all possible choices for i
     */

    private int boundaryIntersection(Coordinate pt, List<Coordinate> poly)
    {

        var res = -1;

        for(var i = 0; i < poly.size() - 1; i++)
        {
            var vi = poly.get(i);
            var vi1 = poly.get(i + 1);
            if(vi.x <= pt.x && pt.x < vi1.x && (res == -1 || northOf(poly.get(res), vi)))
            {
                res = i;
            }
        }

        if(res == -1)
        {
            throw new IllegalArgumentException();
        }

        return res;
    }

}