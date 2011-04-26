// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.PolygonUtils;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

import static java.lang.Double.isNaN;

/**
 * Shape that comprises a prism.  Immutable here but composed with a Property<Polygon> in Prism for mutability.
 *
 * @author Sam Reid
 */
public class Polygon implements IShape {
    private ArrayList<ImmutableVector2D> points = new ArrayList<ImmutableVector2D>();

    //Create a polygon with the specified corners
    public Polygon( ImmutableVector2D[] points ) {
        this( Arrays.asList( points ) );
    }

    //Create a polygon with the specified corners
    public Polygon( List<ImmutableVector2D> points ) {
        this.points = new ArrayList<ImmutableVector2D>( points );
    }

    //Convert to a java.awt.Shape
    public Shape toShape() {
        DoubleGeneralPath path = new DoubleGeneralPath( points.get( 0 ) );
        for ( ImmutableVector2D point : points.subList( 1, points.size() ) ) {
            path.lineTo( point );
        }
        path.closePath();
        return path.getGeneralPath();
    }

    //Get the specified corner point
    public ImmutableVector2D getPoint( int i ) {
        return points.get( i );
    }

    //Create a new Polygon translated by the specified amount
    public IShape getTranslatedInstance( final double dx, final double dy ) {
        return new Polygon( new ArrayList<ImmutableVector2D>() {{
            for ( ImmutableVector2D point : points ) {
                add( point.plus( dx, dy ) );
            }
        }} );
    }

    //Compute the intersections of the specified ray with this polygon's edges
    public ArrayList<Intersection> getIntersections( Ray ray ) {
        ArrayList<Intersection> intersections = new ArrayList<Intersection>();
        for ( Line2D.Double lineSegment : getEdges() ) {
            //Get the intersection if there is one
            Point2D.Double intersection = MathUtil.getLineSegmentsIntersection( lineSegment, new Line2D.Double( ray.tail.toPoint2D(),
                                                                                                                ray.tail.plus( ray.directionUnitVector.times( 1 ) ).toPoint2D() ) );
            if ( intersection != null && !isNaN( intersection.getX() ) && !isNaN( intersection.getY() ) ) {
                //Choose the normal vector that points the opposite direction of the incoming ray
                ImmutableVector2D normal1 = new ImmutableVector2D( lineSegment.getP1(), lineSegment.getP2() ).getRotatedInstance( +Math.PI / 2 ).getNormalizedInstance();
                ImmutableVector2D normal2 = new ImmutableVector2D( lineSegment.getP1(), lineSegment.getP2() ).getRotatedInstance( -Math.PI / 2 ).getNormalizedInstance();
                ImmutableVector2D unitNormal = ray.directionUnitVector.dot( normal1 ) < 0 ? normal1 : normal2;

                //Add to the list of intersections
                intersections.add( new Intersection( unitNormal, new ImmutableVector2D( intersection ) ) );
            }
        }
        return intersections;
    }

    //List all bounding edges in the polygon
    private ArrayList<Line2D.Double> getEdges() {
        ArrayList<Line2D.Double> lineSegments = new ArrayList<Line2D.Double>();
        for ( int i = 0; i < points.size(); i++ ) {
            int next = i == points.size() - 1 ? 0 : i + 1;//make sure to loop from the last point to the first point
            lineSegments.add( new Line2D.Double( points.get( i ).toPoint2D(), points.get( next ).toPoint2D() ) );
        }
        return lineSegments;
    }

    public Rectangle2D getBounds() {
        return toShape().getBounds2D();
    }

    //Gets a rotated copy of this polygon
    public IShape getRotatedInstance( final double angle ) {
        final ImmutableVector2D centroid = getCentroid();//cache for performance
        return new Polygon( new ArrayList<ImmutableVector2D>() {{
            for ( ImmutableVector2D point : points ) {
                ImmutableVector2D vectorAboutCentroid = point.minus( centroid );
                final ImmutableVector2D rotated = vectorAboutCentroid.getRotatedInstance( angle );
                add( rotated.plus( centroid ) );
            }
        }} );
    }

    //Lists the corner points
    private Point2D[] toPointArray() {
        Point2D[] array = new Point2D[points.size()];
        for ( int i = 0; i < array.length; i++ ) {
            array[i] = points.get( i ).toPoint2D();
        }
        return array;
    }

    //Computes the centroid of the corner points (e.g. the center of "mass" assuming the corner points have equal "mass")
    public ImmutableVector2D getCentroid() {
        return new ImmutableVector2D( PolygonUtils.getCentroid( toPointArray() ) );
    }

    //Just use the 0th point for the reference point for rotation drag handles
    public Option<ImmutableVector2D> getReferencePoint() {
        return new Option.Some<ImmutableVector2D>( getPoint( 0 ) );
    }

    public boolean containsPoint( ImmutableVector2D point ) {
        return toShape().contains( point.toPoint2D() );
    }
}