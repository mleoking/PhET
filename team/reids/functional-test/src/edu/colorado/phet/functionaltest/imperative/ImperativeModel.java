// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.imperative;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public class ImperativeModel {
    private final ArrayList<Circle> circles = new ArrayList<Circle>();

    public ImperativeModel( final IClock clock ) {
        for ( int i = 0; i < 10; i++ ) {
            circles.add( new Circle( new Vector2D( i * 60, 100 ) ) );
        }
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                for ( Circle circle : circles ) {
                    circle.stepInTime( clockEvent.getSimulationTimeChange() );
                }
            }
        } );
    }

    public ArrayList<Circle> getCircles() {
        return circles;
    }
}