package edu.colorado.phet.fluidpressureandflow.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

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
}
