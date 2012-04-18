// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;

import static edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState.initialState;

/**
 * The model is the container for the immutable state.
 *
 * @author Sam Reid
 */
public class MatchingGameModel {
    public final GameAudioPlayer gameAudioPlayer = new GameAudioPlayer( true );
    public final Property<MatchingGameState> state = new Property<MatchingGameState>( initialState() ) {{
        addObserver( new ChangeObserver<MatchingGameState>() {
            @Override public void update( final MatchingGameState newValue, final MatchingGameState oldValue ) {
                if ( newValue.info.audio && oldValue.info.mode == Mode.WAITING_FOR_USER_TO_CHECK_ANSWER && newValue.info.mode == Mode.USER_CHECKED_CORRECT_ANSWER ) {
                    gameAudioPlayer.correctAnswer();
                }
                if ( newValue.info.audio && oldValue.info.mode == Mode.WAITING_FOR_USER_TO_CHECK_ANSWER && newValue.info.mode == Mode.SHOWING_WHY_ANSWER_WRONG ) {
                    gameAudioPlayer.wrongAnswer();
                }
            }
        } );
    }};
    public Clock clock = new ConstantDtClock( 60.0 ) {{
        addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                SwingUtilities.invokeLater( new Runnable() {
                    @Override public void run() {
                        final MatchingGameState init = state.get();
                        final MatchingGameState value = init.stepInTime( clockEvent.getSimulationTimeChange() );
//                        System.out.println( "stepping in time, before = " + init.info + ", after = " + value.info );
                        state.set( value );
                    }
                } );
            }
        } );
    }};
}