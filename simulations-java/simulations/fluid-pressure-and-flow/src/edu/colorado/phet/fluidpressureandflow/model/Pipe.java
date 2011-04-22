// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.spline.CubicSpline2D;

/**
 * This is not a pipe.
 *
 * @author Sam Reid
 */
public class Pipe {
    private ArrayList<CrossSection> controlCrossSections = new ArrayList<CrossSection>();
    private ArrayList<CrossSection> splineCrossSections;//Nonlinear interpolation of the control sections
    private boolean dirty = true;//Flag to improve performance

    /**
     * Creates a pipe with a default shape.
     */
    public Pipe() {
        controlCrossSections.add( new CrossSection( -6, -3, -1 ) );
        controlCrossSections.add( new CrossSection( -4, -3, -1 ) );
        controlCrossSections.add( new CrossSection( -2, -3, -1 ) );
        controlCrossSections.add( new CrossSection( 0, -3, -1 ) );
        controlCrossSections.add( new CrossSection( 2, -3, -1 ) );
        controlCrossSections.add( new CrossSection( 4, -3, -1 ) );
        controlCrossSections.add( new CrossSection( 6, -3, -1 ) );
        for ( CrossSection controlPoint : controlCrossSections ) {
            controlPoint.addObserver( new SimpleObserver() {
                public void update() {
                    dirty = true;
                }
            } );
        }
    }

    public ArrayList<CrossSection> getControlCrossSections() {
        return new ArrayList<CrossSection>( controlCrossSections );
    }

    public void addShapeChangeListener( SimpleObserver simpleObserver ) {
        for ( CrossSection pipePosition : controlCrossSections ) {
            pipePosition.addObserver( simpleObserver );
        }
    }

    public Shape getShape() {
        return getShape( getSplineCrossSections() );
    }

    public ArrayList<CrossSection> getSplineCrossSections() {
        if ( dirty ) {
            splineCrossSections = createSpline();
            dirty = false;
        }
        return splineCrossSections;
    }

    /*
     * Creates the set of interpolated cross section samples from the control cross sections.
     */
    private ArrayList<CrossSection> createSpline() {
        ArrayList<CrossSection> pipePositions = new ArrayList<CrossSection>();
        double dx = 0.2;//extend water flow so it looks like it enters the pipe cutaway
        pipePositions.add( new CrossSection( getMinX() - dx, getBottomLeft().getY(), getTopLeft().getY() ) );
        pipePositions.addAll( this.controlCrossSections );
        pipePositions.add( new CrossSection( getMaxX() + dx, getBottomRight().getY(), getTopRight().getY() ) );
        return spline( pipePositions );
    }

    /*
     * Interpolates the specified control points to obtain a smooth set of cross sections
     */
    private ArrayList<CrossSection> spline( ArrayList<CrossSection> controlPoints ) {
        ArrayList<CrossSection> spline = new ArrayList<CrossSection>();
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
            double bottomY = bottomPt.getY();
            double topY = topPt.getY();
            final double min = 0.5;
            if ( topY - bottomY < min ) {
                double center = ( topY + bottomY ) / 2;
                topY = center + min / 2;
                bottomY = center - min / 2;
            }
            spline.add( new CrossSection( ( topPt.getX() + bottomPt.getX() ) / 2, bottomY, topY ) );
        }
        return spline;
    }

    /*
     * Converts a list of CrossSections to a Shape, this is used with the interpolated cross sections.
     */
    public Shape getShape( ArrayList<CrossSection> controlSections ) {
        DoubleGeneralPath path = new DoubleGeneralPath( controlSections.get( 0 ).getTop() );
        for ( CrossSection pipePosition : controlSections.subList( 1, controlSections.size() ) ) {
            path.lineTo( pipePosition.getTop() );
        }

        final ArrayList<CrossSection> rev = new ArrayList<CrossSection>( controlSections ) {{
            Collections.reverse( this );
        }};
        for ( CrossSection pipePosition : rev ) {
            path.lineTo( pipePosition.getBottom() );
        }
        return path.getGeneralPath();
    }

    public Shape getPath( Function1<CrossSection, Point2D> getter ) {
        ArrayList<CrossSection> pipePositions = getSplineCrossSections();
        DoubleGeneralPath path = new DoubleGeneralPath();
        for ( int i = 0; i < pipePositions.size(); i++ ) {
            CrossSection pipePosition = pipePositions.get( i );
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
        return getPath( new Function1<CrossSection, Point2D>() {
            public Point2D apply( CrossSection pipePosition ) {
                return pipePosition.getTop();
            }
        } );
    }

    public Shape getBottomPath() {
        return getPath( new Function1<CrossSection, Point2D>() {
            public Point2D apply( CrossSection pipePosition ) {
                return pipePosition.getBottom();
            }
        } );
    }

    /*
     * Given a global y-position, determine the fraction to the top (point at bottom = 0, point halfway up = 0.5, etc.)
     */
    public double getFractionToTop( double x, double y ) {
        CrossSection position = getCrossSection( x );
        return new Function.LinearFunction( position.getBottom().getY(), position.getTop().getY(), 0, 1 ).evaluate( y );
    }

    /*
     * Determines the cross section for a given x-coordinate by linear interpolation between the nearest nonlinear samples.
     */
    public CrossSection getCrossSection( double x ) {
        CrossSection previous = getPipePositionBefore( x );
        CrossSection next = getPipePositionAfter( x );
        double top = new Function.LinearFunction( previous.getTop(), next.getTop() ).evaluate( x );
        double bottom = new Function.LinearFunction( previous.getBottom(), next.getBottom() ).evaluate( x );
        return new CrossSection( x, bottom, top );
    }

    public double fractionToLocation( double x, double fraction ) {
        CrossSection position = getCrossSection( x );
        return new Function.LinearFunction( 0, 1, position.getBottom().getY(), position.getTop().getY() ).evaluate( fraction );
    }

    private CrossSection getPipePositionBefore( final double x ) {
        ArrayList<CrossSection> list = new ArrayList<CrossSection>() {{
            for ( CrossSection pipePosition : getCrossSections() ) {
                if ( pipePosition.getX() < x ) {
                    add( pipePosition );
                }
            }
        }};
        Collections.sort( list, new Comparator<CrossSection>() {
            public int compare( CrossSection o1, CrossSection o2 ) {
                return Double.compare( Math.abs( x - o1.getX() ), Math.abs( x - o2.getX() ) );
            }
        } );
        if ( list.size() == 0 ) {
            throw new RuntimeException( "No pipe segments before x= " + x );
        }
        return list.get( 0 );
    }

    private Iterable<? extends CrossSection> getCrossSections() {
        return getSplineCrossSections();
    }

    private CrossSection getPipePositionAfter( final double x ) {
        ArrayList<CrossSection> list = new ArrayList<CrossSection>() {{
            for ( CrossSection pipePosition : getCrossSections() ) {
                if ( pipePosition.getX() > x ) {
                    add( pipePosition );
                }
            }
        }};
        Collections.sort( list, new Comparator<CrossSection>() {
            public int compare( CrossSection o1, CrossSection o2 ) {
                return Double.compare( Math.abs( x - o1.getX() ), Math.abs( x - o2.getX() ) );
            }
        } );
        return list.get( 0 );
    }

    public double getSpeed( double x ) {
        //Continuity equation: a1 v1 = a2 v2
        //TODO: treat pipes as if they are cylindrical cross sections?
        double k = 5.0;
        return k / getCrossSection( x ).getHeight();
    }

    public ImmutableVector2D getVelocity( double x, double y ) {
        double speed = getSpeed( x );
        double fraction = getFractionToTop( x, y );
        CrossSection pre = getCrossSection( x - 1E-7 );
        CrossSection post = getCrossSection( x + 1E-7 );

        double x0 = pre.getX();
        double y0 = new Function.LinearFunction( 0, 1, pre.getBottom().getY(), pre.getTop().getY() ).evaluate( fraction );

        double x1 = post.getX();
        double y1 = new Function.LinearFunction( 0, 1, post.getBottom().getY(), post.getTop().getY() ).evaluate( fraction );

        ImmutableVector2D vector2D = new ImmutableVector2D( new Point2D.Double( x0, y0 ), new Point2D.Double( x1, y1 ) );
        return vector2D.getInstanceOfMagnitude( speed );
    }

    public double getMaxX() {
        ArrayList<CrossSection> list = getPipePositionsSortedByX();
        return list.get( list.size() - 1 ).getX();
    }

    public double getMinX() {
        return getPipePositionsSortedByX().get( 0 ).getX();
    }

    private ArrayList<CrossSection> getPipePositionsSortedByX() {
        return new ArrayList<CrossSection>( controlCrossSections ) {{
            Collections.sort( this, new Comparator<CrossSection>() {
                public int compare( CrossSection o1, CrossSection o2 ) {
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
        for ( CrossSection pipePosition : controlCrossSections ) {
            pipePosition.reset();
        }
    }

    /*
     * Gets the x velocity of a particle, incorporating vertical effects.  If this effect is ignored, then when there is a large
     * slope in the pipe, particles closer to the edge move much faster (which is physically incorrect).
     */
    public double getTweakedVx( double x, double y ) {
        ImmutableVector2D velocity = getVelocity( x, y );
        ImmutableVector2D xVelocity = new ImmutableVector2D( velocity.getX(), 0 );
        final double vx = getSpeed( x ) / ( velocity.getMagnitude() / xVelocity.getMagnitude() );
        return vx;
    }

    public ImmutableVector2D getTweakedVelocity( double x, double y ) {
        return new ImmutableVector2D( getTweakedVx( x, y ), getVelocity( x, y ).getY() );
    }

}
