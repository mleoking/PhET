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
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.spline.CubicSpline2D;

/**
 * This is not a pipe.
 *
 * @author Sam Reid
 */
public class Pipe {
    private ArrayList<CrossSection> controlPoints = new ArrayList<CrossSection>();
    private ArrayList<CrossSection> spline;
    private boolean dirty = true;

    public Pipe() {
        controlPoints.add( new CrossSection( -6, -3, -1 ) );
        controlPoints.add( new CrossSection( -4, -3, -1 ) );
        controlPoints.add( new CrossSection( -2, -3, -1 ) );
        controlPoints.add( new CrossSection( 0, -3, -1 ) );
        controlPoints.add( new CrossSection( 2, -3, -1 ) );
        controlPoints.add( new CrossSection( 4, -3, -1 ) );
        controlPoints.add( new CrossSection( 6, -3, -1 ) );
        for ( CrossSection controlPoint : controlPoints ) {
            controlPoint.addObserver( new SimpleObserver() {
                public void update() {
                    dirty = true;
                }
            } );
        }
    }

    public ArrayList<CrossSection> getControlPoints() {
        return new ArrayList<CrossSection>( controlPoints );
    }

    public void addShapeChangeListener( SimpleObserver simpleObserver ) {
        for ( CrossSection pipePosition : controlPoints ) {
            pipePosition.addObserver( simpleObserver );
        }
    }

    public Shape getShape() {
//        return getShape( getAugmentedPipePositionArray() ); //non spline version
        return getShape( getAugmentedSplinePipePositionArray() );
    }

    private ArrayList<CrossSection> getAugmentedPipePositionArray() {
        ArrayList<CrossSection> pipePositions = new ArrayList<CrossSection>();
        double dx = 0.2;//extend water flow so it looks like it enters the pipe cutaway
        pipePositions.add( new CrossSection( getMinX() - dx, getBottomLeft().getY(), getTopLeft().getY() ) );
        pipePositions.addAll( this.controlPoints );
        pipePositions.add( new CrossSection( getMaxX() + dx, getBottomRight().getY(), getTopRight().getY() ) );
        return pipePositions;
    }

    public ArrayList<CrossSection> getAugmentedSplinePipePositionArray() {
        if ( dirty ) {
            spline = createSpline();
            dirty = false;
        }
        return spline;
    }

    private ArrayList<CrossSection> createSpline() {
        ArrayList<CrossSection> pipePositions = new ArrayList<CrossSection>();
        double dx = 0.2;//extend water flow so it looks like it enters the pipe cutaway
        pipePositions.add( new CrossSection( getMinX() - dx, getBottomLeft().getY(), getTopLeft().getY() ) );
        pipePositions.addAll( this.controlPoints );
        pipePositions.add( new CrossSection( getMaxX() + dx, getBottomRight().getY(), getTopRight().getY() ) );
        return spline( pipePositions );
    }

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

    public Shape getShape( ArrayList<CrossSection> pipePositions ) {
        DoubleGeneralPath path = new DoubleGeneralPath( pipePositions.get( 0 ).getTop() );
        for ( CrossSection pipePosition : pipePositions.subList( 1, pipePositions.size() ) ) {
            path.lineTo( pipePosition.getTop() );
        }

        final ArrayList<CrossSection> rev = new ArrayList<CrossSection>( pipePositions ) {{
            Collections.reverse( this );
        }};
        for ( CrossSection pipePosition : rev ) {
            path.lineTo( pipePosition.getBottom() );
        }
        return path.getGeneralPath();
    }

    public Shape getPath( Function1<CrossSection, Point2D> getter ) {
        ArrayList<CrossSection> pipePositions = getAugmentedSplinePipePositionArray();
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

    /**
     * Given a global y-position, determine the fraction to the top (point at bottom = 0, point halfway up = 0.5, etc.)
     *
     * @param y
     * @return
     */
    public double getFractionToTop( double x, double y ) {
        CrossSection position = getPipePosition( x );
        return new Function.LinearFunction( position.getBottom().getY(), position.getTop().getY(), 0, 1 ).evaluate( y );
    }

    public CrossSection getPipePosition( double x ) {
        CrossSection previous = getPipePositionBefore( x );
        CrossSection next = getPipePositionAfter( x );
        double top = new Function.LinearFunction( previous.getTop(), next.getTop() ).evaluate( x );
        double bottom = new Function.LinearFunction( previous.getBottom(), next.getBottom() ).evaluate( x );
        return new CrossSection( x, bottom, top );
    }

    public double fractionToLocation( double x, double fraction ) {
        CrossSection position = getPipePosition( x );
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
        return getAugmentedSplinePipePositionArray();
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
        return k / getPipePosition( x ).getHeight();
    }

    public ImmutableVector2D getVelocity( double x, double y ) {
        double speed = getSpeed( x );
        double fraction = getFractionToTop( x, y );
        CrossSection pre = getPipePosition( x - 1E-7 );
        CrossSection post = getPipePosition( x + 1E-7 );

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
        return new ArrayList<CrossSection>( controlPoints ) {{
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
        return new Point2D.Double( getMinX(), getControlPoints().get( 0 ).getTop().getY() );
    }

    public Point2D getBottomLeft() {
        return new Point2D.Double( getMinX(), getControlPoints().get( 0 ).getBottom().getY() );
    }

    public Point2D getTopRight() {
        return new Point2D.Double( getMaxX(), getControlPoints().get( getControlPoints().size() - 1 ).getTop().getY() );
    }

    public Point2D getBottomRight() {
        return new Point2D.Double( getMaxX(), getControlPoints().get( getControlPoints().size() - 1 ).getBottom().getY() );
    }

    public void reset() {
        for ( CrossSection pipePosition : controlPoints ) {
            pipePosition.reset();
        }
    }

    /*
     * Gets the x velocity of a particle, incorporating vertical effects.  If this effect is ignored, then when there is a large
     * slope in the pipe, particles closer to the edge move much faster (which is physically incorrect).
     *
     * @return the x velocity
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
