// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

import static java.lang.Double.isNaN;

/**
 * @author Sam Reid
 */
public class Polygon {
    private ArrayList<ImmutableVector2D> points = new ArrayList<ImmutableVector2D>();

    public Polygon( ArrayList<ImmutableVector2D> points ) {
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
                //TODO: this unit vector should point toward the incoming ray (though maybe doesn't matter?), see http://en.wikipedia.org/wiki/Snell's_law#Vector_form
                ImmutableVector2D unitNormal = new ImmutableVector2D( lineSegment.getP1(), lineSegment.getP2() ).getRotatedInstance( -Math.PI / 2 ).getNormalizedInstance();
                intersections.add( new Intersection( unitNormal, new ImmutableVector2D( intersection ) ) );
            }
        }
        return intersections;
    }
}
