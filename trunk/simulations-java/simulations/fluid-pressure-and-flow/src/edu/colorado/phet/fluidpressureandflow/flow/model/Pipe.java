// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.model;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.spline.CubicSpline2D;
import edu.colorado.phet.fluidpressureandflow.flow.view.PipeCrossSectionControl;

import static edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents.*;
import static java.util.Collections.min;

/**
 * This is not a pipe.  The left of the pipe is x=0 and the control points can be moved up and down to deform the pipe.
 *
 * @author Sam Reid
 */
public class Pipe {

    //Cross sections that the user can manipulate to deform the pipe.
    private final ArrayList<PipeCrossSection> controlCrossSections = new ArrayList<PipeCrossSection>();

    //Nonlinear interpolation of the control sections for particle motion and determining the velocity field
    private ArrayList<PipeCrossSection> splineCrossSections;

    //Flag to improve performance
    private boolean dirty = true;

    //Rate of fluid flow in volume (m^3) per second
    public final DoubleProperty flowRate = new DoubleProperty( 5.0 );

    //Flag indicating whether friction should slow particles near the edges
    public final Property<Boolean> friction = new Property<Boolean>( false );

    //Creates a pipe with a default shape.
    public Pipe() {
        controlCrossSections.add( new PipeCrossSection( pipeCrossSectionHandle1, -6, -3, -1 ) );
        controlCrossSections.add( new PipeCrossSection( pipeCrossSectionHandle2, -4, -3, -1 ) );
        controlCrossSections.add( new PipeCrossSection( pipeCrossSectionHandle3, -2, -3, -1 ) );
        controlCrossSections.add( new PipeCrossSection( pipeCrossSectionHandle4, 0, -3, -1 ) );
        controlCrossSections.add( new PipeCrossSection( pipeCrossSectionHandle5, 2, -3, -1 ) );
        controlCrossSections.add( new PipeCrossSection( pipeCrossSectionHandle6, 4, -3, -1 ) );
        controlCrossSections.add( new PipeCrossSection( pipeCrossSectionHandle7, 6, -3, -1 ) );
        for ( PipeCrossSection controlPoint : controlCrossSections ) {
            controlPoint.addObserver( new SimpleObserver() {
                public void update() {
                    dirty = true;
                }
            } );
        }
    }

    public ArrayList<PipeCrossSection> getControlCrossSections() {
        return new ArrayList<PipeCrossSection>( controlCrossSections );
    }

    public void addShapeChangeListener( SimpleObserver simpleObserver ) {
        for ( PipeCrossSection pipePosition : controlCrossSections ) {
            pipePosition.addObserver( simpleObserver );
        }
    }

    public Shape getShape() {
        return getShape( getSplineCrossSections() );
    }

    public ArrayList<PipeCrossSection> getSplineCrossSections() {
        if ( dirty ) {
            splineCrossSections = createSpline();
            dirty = false;
        }
        return splineCrossSections;
    }

    //Creates the set of interpolated cross section samples from the control cross sections.
    private ArrayList<PipeCrossSection> createSpline() {
        ArrayList<PipeCrossSection> pipePositions = new ArrayList<PipeCrossSection>();
        double dx = 0.2;//extend water flow so it looks like it enters the pipe cutaway
        pipePositions.add( new PipeCrossSection( null, getMinX() - dx, getBottomLeft().getY(), getTopLeft().getY() ) );
        pipePositions.addAll( this.controlCrossSections );
        pipePositions.add( new PipeCrossSection( null, getMaxX() + dx, getBottomRight().getY(), getTopRight().getY() ) );
        return spline( pipePositions );
    }

    //Interpolates the specified control points to obtain a smooth set of cross sections
    private ArrayList<PipeCrossSection> spline( ArrayList<PipeCrossSection> controlPoints ) {
        ArrayList<PipeCrossSection> spline = new ArrayList<PipeCrossSection>();
        SerializablePoint2D[] top = new SerializablePoint2D[controlPoints.size()];
        for ( int i = 0; i < top.length; i++ ) {
            top[i] = new SerializablePoint2D( controlPoints.get( i ).getTop() );
        }
        CubicSpline2D topSpline = new CubicSpline2D( top );

        SerializablePoint2D[] bottom = new SerializablePoint2D[controlPoints.size()];
        for ( int i = 0; i < bottom.length; i++ ) {
            bottom[i] = new SerializablePoint2D( controlPoints.get( i ).getBottom() );
        }
        CubicSpline2D bottomSpline = new CubicSpline2D( bottom );
        for ( double alpha = 0; alpha <= 1; alpha += 1.0 / 70 ) {
            SerializablePoint2D topPt = topSpline.evaluate( alpha );
            SerializablePoint2D bottomPt = bottomSpline.evaluate( alpha );

            //make sure pipe top doesn't go below pipe bottom
            //Note that when the velocity becomes too high, Bernoulli's equation gives a negative pressure.
            //The pressure doesn't really go negative then, it just means Bernoulli's equation is inapplicable in that situation
            //So we have to make sure the distance threshold is high enough that Bernoulli's equation never gives a negative pressure
            final double min = PipeCrossSectionControl.DISTANCE_THRESHOLD;
            double bottomY = bottomPt.getY();
            double topY = topPt.getY();
            if ( topY - bottomY < min ) {
                double center = ( topY + bottomY ) / 2;
                topY = center + min / 2;
                bottomY = center - min / 2;
            }
            spline.add( new PipeCrossSection( null, ( topPt.getX() + bottomPt.getX() ) / 2, bottomY, topY ) );
        }
        return spline;
    }

    //Converts a list of CrossSections to a Shape, this is used with the interpolated cross sections.
    public Shape getShape( ArrayList<PipeCrossSection> controlSections ) {
        DoubleGeneralPath path = new DoubleGeneralPath( controlSections.get( 0 ).getTop() );
        for ( PipeCrossSection pipePosition : controlSections.subList( 1, controlSections.size() ) ) {
            path.lineTo( pipePosition.getTop() );
        }

        final ArrayList<PipeCrossSection> rev = new ArrayList<PipeCrossSection>( controlSections ) {{
            Collections.reverse( this );
        }};
        for ( PipeCrossSection pipePosition : rev ) {
            path.lineTo( pipePosition.getBottom() );
        }
        return path.getGeneralPath();
    }

    //Gets a single path for the top or bottom of the pipe
    public Shape getPath( Function1<PipeCrossSection, Point2D> getter ) {
        ArrayList<PipeCrossSection> pipePositions = getSplineCrossSections();
        DoubleGeneralPath path = new DoubleGeneralPath();
        for ( int i = 0; i < pipePositions.size(); i++ ) {
            PipeCrossSection pipePosition = pipePositions.get( i );
            if ( i == 0 ) {
                path.moveTo( getter.apply( pipePosition ) );
            }
            else {
                path.lineTo( getter.apply( pipePosition ) );
            }
        }
        return path.getGeneralPath();
    }

    public Shape getTopPath() {
        return getPath( new Function1<PipeCrossSection, Point2D>() {
            public Point2D apply( PipeCrossSection pipePosition ) {
                return pipePosition.getTop();
            }
        } );
    }

    public Shape getBottomPath() {
        return getPath( new Function1<PipeCrossSection, Point2D>() {
            public Point2D apply( PipeCrossSection pipePosition ) {
                return pipePosition.getBottom();
            }
        } );
    }

    //Given a global y-position, determine the fraction to the top (point at bottom = 0, point halfway up = 0.5, etc.)
    public double getFractionToTop( double x, double y ) {
        PipeCrossSection position = getCrossSection( x );
        return new Function.LinearFunction( position.getBottom().getY(), position.getTop().getY(), 0, 1 ).evaluate( y );
    }

    //Determines the cross section for a given x-coordinate by linear interpolation between the nearest nonlinear samples.
    public PipeCrossSection getCrossSection( double x ) {
        PipeCrossSection previous = getPipePositionBefore( x );
        PipeCrossSection next = getPipePositionAfter( x );
        double top = new Function.LinearFunction( previous.getTop(), next.getTop() ).evaluate( x );
        double bottom = new Function.LinearFunction( previous.getBottom(), next.getBottom() ).evaluate( x );
        return new PipeCrossSection( null, x, bottom, top );
    }

    //Find the y-value for the specified x-value and fraction (0=bottom, 1=top) of the pipe
    public double fractionToLocation( double x, double fraction ) {
        PipeCrossSection position = getCrossSection( x );
        return new Function.LinearFunction( 0, 1, position.getBottom().getY(), position.getTop().getY() ).evaluate( fraction );
    }

    //Lookup the cross section immediately before the specified x-location for interpolation
    private PipeCrossSection getPipePositionBefore( final double x ) {
        ArrayList<PipeCrossSection> list = new ArrayList<PipeCrossSection>() {{
            for ( PipeCrossSection pipePosition : getCrossSections() ) {
                if ( pipePosition.getX() < x ) {
                    add( pipePosition );
                }
            }
        }};
        if ( list.size() == 0 ) {
            throw new RuntimeException( "No pipe segments before x= " + x );
        }
        return min( list, new PipeCrossSectionXComparator( x ) );
    }

    private Iterable<? extends PipeCrossSection> getCrossSections() {
        return getSplineCrossSections();
    }

    //Lookup the cross section immediately after the specified x-location for interpolation
    private PipeCrossSection getPipePositionAfter( final double x ) {
        ArrayList<PipeCrossSection> list = new ArrayList<PipeCrossSection>() {{
            for ( PipeCrossSection pipePosition : getCrossSections() ) {
                if ( pipePosition.getX() > x ) {
                    add( pipePosition );
                }
            }
        }};
        return Collections.min( list, new PipeCrossSectionXComparator( x ) );
    }

    static class PipeCrossSectionXComparator implements Comparator<PipeCrossSection> {
        private final double x;

        PipeCrossSectionXComparator( double x ) {
            this.x = x;
        }

        public int compare( PipeCrossSection o1, PipeCrossSection o2 ) {
            return Double.compare( Math.abs( x - o1.getX() ), Math.abs( x - o2.getX() ) );
        }
    }

    public double getMaxX() {
        ArrayList<PipeCrossSection> list = getPipePositionsSortedByX();
        return list.get( list.size() - 1 ).getX();
    }

    public double getMinX() {
        return getPipePositionsSortedByX().get( 0 ).getX();
    }

    private ArrayList<PipeCrossSection> getPipePositionsSortedByX() {
        return new ArrayList<PipeCrossSection>( controlCrossSections ) {{
            Collections.sort( this, new Comparator<PipeCrossSection>() {
                public int compare( PipeCrossSection o1, PipeCrossSection o2 ) {
                    return Double.compare( o1.getX(), o2.getX() );
                }
            } );
        }};
    }

    public boolean contains( double x, double y ) {
        return getShape().contains( x, y );
    }

    public Point2D getTopLeft() {
        return new Point2D.Double( getMinX(), getControlCrossSections().get( 0 ).getTop().getY() );
    }

    public Point2D getBottomLeft() {
        return new Point2D.Double( getMinX(), getControlCrossSections().get( 0 ).getBottom().getY() );
    }

    public Point2D getTopRight() {
        return new Point2D.Double( getMaxX(), getControlCrossSections().get( getControlCrossSections().size() - 1 ).getTop().getY() );
    }

    public Point2D getBottomRight() {
        return new Point2D.Double( getMaxX(), getControlCrossSections().get( getControlCrossSections().size() - 1 ).getBottom().getY() );
    }

    public void reset() {
        flowRate.reset();
        friction.reset();
        for ( PipeCrossSection pipePosition : controlCrossSections ) {
            pipePosition.reset();
        }
    }

    //Get the speed at the specified x-location.  This is before friction and vertical effects are accounted for
    private double getSpeed( double x ) {

        //Continuity equation: a1 v1 = a2 v2
        //treat pipes as if they are cylindrical cross sections?
        final double crossSectionDiameter = getCrossSection( x ).getHeight();
        double crossSectionRadius = crossSectionDiameter / 2;
        double crossSectionArea = Math.PI * crossSectionRadius * crossSectionRadius;
        return flowRate.get() / crossSectionArea;
    }

    //I was told that the fluid flow rate falls off quadratically, so use lagrange interpolation so that at the center of the pipe
    //The velocity is full speed, and it falls off quadratically toward the sides
    //See http://stackoverflow.com/questions/2075013/best-way-to-find-quadratic-regression-curve-in-java
    public double lagrange( double x1, double y1, double x2, double y2, double x3, double y3, double x ) {
        return ( x - x2 ) * ( x - x3 ) / ( x1 - x2 ) / ( x1 - x3 ) * y1 +
               ( x - x1 ) * ( x - x3 ) / ( x2 - x1 ) / ( x2 - x3 ) * y2 +
               ( x - x1 ) * ( x - x2 ) / ( x3 - x1 ) / ( x3 - x2 ) * y3;
    }

    //Get the velocity at the specified point, does not account for vertical effects or friction.
    private ImmutableVector2D getVelocity( double x, double y ) {
        double fraction = getFractionToTop( x, y );
        double speed = getSpeed( x );

        PipeCrossSection pre = getCrossSection( x - 1E-7 );
        PipeCrossSection post = getCrossSection( x + 1E-7 );

        double x0 = pre.getX();
        double y0 = new Function.LinearFunction( 0, 1, pre.getBottom().getY(), pre.getTop().getY() ).evaluate( fraction );

        double x1 = post.getX();
        double y1 = new Function.LinearFunction( 0, 1, post.getBottom().getY(), post.getTop().getY() ).evaluate( fraction );

        ImmutableVector2D vector2D = new ImmutableVector2D( new Point2D.Double( x0, y0 ), new Point2D.Double( x1, y1 ) );
        return vector2D.getInstanceOfMagnitude( speed );
    }

    //Gets the x-velocity of a particle, incorporating vertical effects.
    //If this effect is ignored, then when there is a large slope in the pipe, particles closer to the edge move much faster (which is physically incorrect).
    public double getTweakedVx( double x, double y ) {
        ImmutableVector2D velocity = getVelocity( x, y );
        ImmutableVector2D xVelocity = new ImmutableVector2D( velocity.getX(), 0 );
        double vx = getSpeed( x ) / ( velocity.getMagnitude() / xVelocity.getMagnitude() );

        //If friction is enabled, then scale down quadratically (like a parabola) as you get further from the center of the pipe.
        //But instead of reaching zero velocity at the edge of the pipe (which could cause particles to pile up indefinitely), extend the region
        //a small epsilon past the (0..1) pipe range
        if ( friction.get() ) {
            double epsilon = 0.2;
            double fractionToTop = getFractionToTop( x, y );
            double scaleFactor = lagrange( -epsilon, 0, 0.5, 1, 1 + epsilon, 0, fractionToTop );
            return vx * scaleFactor;
        }
        else {
            return vx;
        }
    }

    public ImmutableVector2D getTweakedVelocity( double x, double y ) {
        return new ImmutableVector2D( getTweakedVx( x, y ), getVelocity( x, y ).getY() );
    }

    //Get the point at the specified location, where x is in meters and fractionToTop is in (0,1)
    public ImmutableVector2D getPoint( double x, double fractionToTop ) {
        return new ImmutableVector2D( x, fractionToLocation( x, fractionToTop ) );
    }

    //Compute the circular cross sectional area (in meters squared) at the specified location
    public double getCrossSectionalArea( double x ) {
        double radius = Math.abs( getPoint( x, 0.5 ).getY() - getPoint( x, 1 ).getY() );
        return Math.PI * radius * radius;
    }
}