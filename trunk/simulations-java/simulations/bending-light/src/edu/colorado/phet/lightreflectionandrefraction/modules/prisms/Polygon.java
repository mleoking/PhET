// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

import static java.lang.Double.isNaN;

/**
 * @author Sam Reid
 */
public class Polygon {
    private ArrayList<ImmutableVector2D> points = new ArrayList<ImmutableVector2D>();

    public Polygon( ImmutableVector2D[] vectors ) {
        this( Arrays.asList( vectors ) );
    }

    public Polygon( List<ImmutableVector2D> points ) {
        this.points = new ArrayList<ImmutableVector2D>( points );
    }

    public Shape toShape() {
        DoubleGeneralPath path = new DoubleGeneralPath( points.get( 0 ) );
        for ( ImmutableVector2D point : points.subList( 1, points.size() ) ) {
            path.lineTo( point );
        }
        path.closePath();
        return path.getGeneralPath();
    }

    public Polygon getTranslatedInstance( final double dx, final double dy ) {
        return new Polygon( new ArrayList<ImmutableVector2D>() {{
            for ( ImmutableVector2D point : points ) {
                add( point.getAddedInstance( dx, dy ) );
            }
        }} );
    }

    public ArrayList<Intersection> getIntersections( Ray incidentRay ) {
        ArrayList<Intersection> intersections = new ArrayList<Intersection>();
        ArrayList<Line2D.Double> lineSegments = new ArrayList<Line2D.Double>();
        for ( int i = 0; i < points.size(); i++ ) {
            int next = i == points.size() - 1 ? 0 : i + 1;
            lineSegments.add( new Line2D.Double( points.get( i ).toPoint2D(), points.get( next ).toPoint2D() ) );
        }
        for ( Line2D.Double lineSegment : lineSegments ) {
            Point2D.Double intersection = MathUtil.getLineSegmentsIntersection( lineSegment, new Line2D.Double( incidentRay.tail.toPoint2D(),
                                                                                                                incidentRay.tail.getAddedInstance( incidentRay.directionUnitVector.getScaledInstance( 1 ) ).toPoint2D() ) );
            if ( intersection != null && !isNaN( intersection.getX() ) && !isNaN( intersection.getY() ) ) {
                //Choose the normal vector that points the opposite direction of the incoming ray
                ImmutableVector2D normal1 = new ImmutableVector2D( lineSegment.getP1(), lineSegment.getP2() ).getRotatedInstance( +Math.PI / 2 ).getNormalizedInstance();
                ImmutableVector2D normal2 = new ImmutableVector2D( lineSegment.getP1(), lineSegment.getP2() ).getRotatedInstance( -Math.PI / 2 ).getNormalizedInstance();
                ImmutableVector2D unitNormal = incidentRay.directionUnitVector.dot( normal1 ) < 0 ? normal1 : normal2;

                intersections.add( new Intersection( unitNormal, new ImmutableVector2D( intersection ) ) );
            }
        }
        return intersections;
    }

    public Rectangle2D getBounds() {
        return toShape().getBounds2D();
    }
}
