// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;

import static edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState.initialState;

/**
 * The model contains a sequence of immutable states.
 *
 * @author Sam Reid
 */
public class MatchingGameModel {
    public final Property<MatchingGameState> state = new Property<MatchingGameState>( initialState() );
    public Clock clock = new ConstantDtClock( 60 ) {{
        addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                state.set( state.get().stepInTime( clockEvent.getSimulationTimeChange() ) );
            }
        } );
    }};
}
