// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;

import static edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState.initialState;
import static edu.colorado.phet.fractionsintro.matchinggame.view.MatchingGameAudio.playAudioClip;

/**
 * The model is the container for the immutable state.
 *
 * @author Sam Reid
 */
public class MatchingGameModel {
    public final Property<MatchingGameState> state = new Property<MatchingGameState>( initialState() ) {{
        addObserver( new ChangeObserver<MatchingGameState>() {
            @Override public void update( final MatchingGameState newValue, final MatchingGameState oldValue ) {
                if ( newValue.audio && oldValue.getLeftScaleValue() == 0.0 && newValue.getLeftScaleValue() > 0 ) {
                    playAudioClip( newValue.getLeftScaleValue() );
                }
                if ( newValue.audio && oldValue.getRightScaleValue() == 0.0 && newValue.getRightScaleValue() > 0 ) {
                    playAudioClip( newValue.getRightScaleValue() );
                }
            }
        } );
    }};
    public Clock clock = new ConstantDtClock( 60 ) {{
        addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                state.set( state.get().stepInTime( clockEvent.getSimulationTimeChange() ) );
            }
        } );
    }};
}