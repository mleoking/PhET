package edu.colorado.phet.fluidpressureandflow.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * @author Sam Reid
 */
public class Pipe {
    private ArrayList<PipePosition> pipePositions = new ArrayList<PipePosition>();

    public Pipe() {
        reset();
    }

    public ArrayList<PipePosition> getPipePositions() {
        return new ArrayList<PipePosition>( pipePositions );
    }

    public void addShapeChangeListener( SimpleObserver simpleObserver ) {
        for ( PipePosition pipePosition : pipePositions ) {
            pipePosition.addObserver( simpleObserver );
        }
    }

    public Shape getShape() {
        DoubleGeneralPath path = new DoubleGeneralPath( pipePositions.get( 0 ).getTop() );
        for ( PipePosition pipePosition : pipePositions.subList( 1, pipePositions.size() ) ) {
            path.lineTo( pipePosition.getTop() );
        }

        final ArrayList<PipePosition> rev = new ArrayList<PipePosition>( pipePositions ) {{
            Collections.reverse( this );
        }};
        for ( PipePosition pipePosition : rev ) {
            path.lineTo( pipePosition.getBottom() );
        }
        return path.getGeneralPath();
    }

    /**
     * Given a global y-position, determine the fraction to the top (point at bottom = 0, point halfway up = 0.5, etc.)
     *
     * @param y
     * @return
     */
    public double getFractionToTop( double x, double y ) {
        PipePosition position = getPipePosition( x );
        return new Function.LinearFunction( position.getBottom().getY(), position.getTop().getY(), 0, 1 ).evaluate( y );
    }

    public PipePosition getPipePosition( double x ) {
        PipePosition previous = getPipePositionBefore( x );
        PipePosition next = getPipePositionAfter( x );
        double top = new Function.LinearFunction( previous.getTop(), next.getTop() ).evaluate( x );
        double bottom = new Function.LinearFunction( previous.getBottom(), next.getBottom() ).evaluate( x );
        return new PipePosition( x, bottom, top );
    }

    //TODO consolidate with above
    public double fractionToLocation( double x, double fraction ) {
        PipePosition position = getPipePosition( x );
        return new Function.LinearFunction( 0, 1, position.getBottom().getY(), position.getTop().getY() ).evaluate( fraction );
    }

    private PipePosition getPipePositionBefore( final double x ) {
        ArrayList<PipePosition> list = new ArrayList<PipePosition>() {{
            for ( PipePosition pipePosition : pipePositions ) {
                if ( pipePosition.getX() < x ) {
                    add( pipePosition );
                }
            }
        }};
        Collections.sort( list, new Comparator<PipePosition>() {
            public int compare( PipePosition o1, PipePosition o2 ) {
                return Double.compare( Math.abs( x - o1.getX() ), Math.abs( x - o2.getX() ) );
            }
        } );
        return list.get( 0 );
    }

    private PipePosition getPipePositionAfter( final double x ) {
        ArrayList<PipePosition> list = new ArrayList<PipePosition>() {{
            for ( PipePosition pipePosition : pipePositions ) {
                if ( pipePosition.getX() > x ) {
                    add( pipePosition );
                }
            }
        }};
        Collections.sort( list, new Comparator<PipePosition>() {
            public int compare( PipePosition o1, PipePosition o2 ) {
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
        PipePosition pre = getPipePosition( x - 1E-7 );
        PipePosition post = getPipePosition( x + 1E-7 );

        double x0 = pre.getX();
        double y0 = new Function.LinearFunction( 0, 1, pre.getBottom().getY(), pre.getTop().getY() ).evaluate( fraction );

        double x1 = post.getX();
        double y1 = new Function.LinearFunction( 0, 1, post.getBottom().getY(), post.getTop().getY() ).evaluate( fraction );

        ImmutableVector2D vector2D = new ImmutableVector2D( new Point2D.Double( x0, y0 ), new Point2D.Double( x1, y1 ) );
        return vector2D.getInstanceOfMagnitude( speed );
    }

    public double getMaxX() {
        ArrayList<PipePosition> list = getPipePositionsSortedByX();
        return list.get( list.size() - 1 ).getX();
    }

    public double getMinX() {
        return getPipePositionsSortedByX().get( 0 ).getX();
    }

    private ArrayList<PipePosition> getPipePositionsSortedByX() {
        return new ArrayList<PipePosition>( pipePositions ) {{
            Collections.sort( this, new Comparator<PipePosition>() {
                public int compare( PipePosition o1, PipePosition o2 ) {
                    return Double.compare( o1.getX(), o2.getX() );
                }
            } );
        }};
    }

    public boolean contains( double x, double y ) {
        return getShape().contains( x, y );
    }

    public Point2D getTopLeft() {
        return new Point2D.Double( getMinX(), getPipePositions().get( 0 ).getTop().getY() );
    }

    public Point2D getBottomLeft() {
        return new Point2D.Double( getMinX(), getPipePositions().get( 0 ).getBottom().getY() );
    }

    public Point2D getTopRight() {
        return new Point2D.Double( getMaxX(), getPipePositions().get( getPipePositions().size() - 1 ).getTop().getY() );
    }

    public Point2D getBottomRight() {
        return new Point2D.Double( getMaxX(), getPipePositions().get( getPipePositions().size() - 1 ).getBottom().getY() );
    }

    public void reset() {
        pipePositions.clear();
        pipePositions.add( new PipePosition( -6, -3, -1 ) );
        pipePositions.add( new PipePosition( -4, -3, -1 ) );
        pipePositions.add( new PipePosition( -2, -3, -1 ) );
        pipePositions.add( new PipePosition( 0, -3, -1 ) );
        pipePositions.add( new PipePosition( 2, -3, -1 ) );
        pipePositions.add( new PipePosition( 4, -3, -1 ) );
        pipePositions.add( new PipePosition( 6, -3, -1 ) );
    }

    /**
     * Gets the x velocity of a particle, incorporating vertical effects.  If this effect is ignored, then when there is a large
     * slope in the pipe, particles closer to the edge move much faster (which is physicially incorrect).
     *
     * @return
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
