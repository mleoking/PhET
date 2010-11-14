package edu.colorado.phet.fluidpressureandflow.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * @author Sam Reid
 */
public class Pipe {
    private ArrayList<PipePosition> pipePositions = new ArrayList<PipePosition>();

    public Pipe() {
        pipePositions.add( new PipePosition( -5, -3, 3 ) );
        pipePositions.add( new PipePosition( -3, -3, 3 ) );
        pipePositions.add( new PipePosition( -1, -3, 3 ) );
        pipePositions.add( new PipePosition( 1, -3, 3 ) );
        pipePositions.add( new PipePosition( 3, -3, 3 ) );
        pipePositions.add( new PipePosition( 5, -3, 3 ) );
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
        PipePosition previous = getPipePositionBefore( x );
        PipePosition next = getPipePositionAfter( x );
        double top = new Function.LinearFunction( previous.getTop(), next.getTop() ).evaluate( x );
        double bottom = new Function.LinearFunction( previous.getBottom(), next.getBottom() ).evaluate( x );
        return new Function.LinearFunction( bottom, top, 0, 1 ).evaluate( y );
    }

    //TODO consolidate with above
    public double fractionToLocation( double x, double fraction ) {
        PipePosition previous = getPipePositionBefore( x );
        PipePosition next = getPipePositionAfter( x );
        double top = new Function.LinearFunction( previous.getTop(), next.getTop() ).evaluate( x );
        double bottom = new Function.LinearFunction( previous.getBottom(), next.getBottom() ).evaluate( x );
        return new Function.LinearFunction( 0, 1, bottom, top ).evaluate( fraction );
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

    public double getVelocity( double x ) {
        return 1;
    }
}
