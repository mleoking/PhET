// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import fj.data.List;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.CompositeBooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.CompositeIntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

import static edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState.initialState;

/**
 * The model is the container for the immutable state of the model.
 *
 * @author Sam Reid
 */
public class MatchingGameModel {

    //System that plays audio when the user makes a correct guess (and audio is enabled)
    private final GameAudioPlayer gameAudioPlayer = new GameAudioPlayer( true );

    //State of the game, including position of all objects.
    public final Property<MatchingGameState> state = new Property<MatchingGameState>( initialState() ) {{
        addObserver( new ChangeObserver<MatchingGameState>() {
            public void update( final MatchingGameState newValue, final MatchingGameState oldValue ) {
                if ( newValue.info.audio && oldValue.info.mode == Mode.WAITING_FOR_USER_TO_CHECK_ANSWER && newValue.info.mode == Mode.USER_CHECKED_CORRECT_ANSWER ) {
                    gameAudioPlayer.correctAnswer();
                }
                if ( newValue.info.audio && oldValue.info.mode == Mode.WAITING_FOR_USER_TO_CHECK_ANSWER && newValue.info.mode == Mode.SHOWING_WHY_ANSWER_WRONG ) {
                    gameAudioPlayer.wrongAnswer();
                }
            }
        } );
    }};

    //Clock that runs the sim.
    public final Clock clock = new ConstantDtClock( 60.0 ) {{
        addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        state.set( state.get().stepInTime( clockEvent.getSimulationTimeChange() ) );
                    }
                } );
            }
        } );
    }};

    //Public interface for observable properties on the sequence of immutable model states
    //This is meant to be read with IDEA's closure folding feature, so that each declaration takes one line
    public final ObservableProperty<Double> leftScaleValue = doubleProperty( new F<MatchingGameState, Double>() {
        @Override public Double f( final MatchingGameState s ) {
            return s.getLeftScaleValue();
        }
    } );
    public final ObservableProperty<Double> rightScaleValue = doubleProperty( new F<MatchingGameState, Double>() {
        @Override public Double f( final MatchingGameState s ) {
            return s.getRightScaleValue();
        }
    } );
    public final ObservableProperty<Integer> checks = intProperty( new F<MatchingGameState, Integer>() {
        @Override public Integer f( final MatchingGameState s ) {
            return s.getChecks();
        }
    } );
    public final ObservableProperty<Integer> scored = intProperty( new F<MatchingGameState, Integer>() {
        @Override public Integer f( final MatchingGameState s ) {
            return s.scored;
        }
    } );
    public final ObservableProperty<Integer> level = intProperty( new F<MatchingGameState, Integer>() {
        @Override public Integer f( final MatchingGameState s ) {
            return s.info.level;
        }
    } );
    public final ObservableProperty<Integer> score = intProperty( new F<MatchingGameState, Integer>() {
        @Override public Integer f( final MatchingGameState s ) {
            return s.info.score;
        }
    } );
    public final ObservableProperty<Boolean> timerVisible = booleanProperty( new F<MatchingGameState, Boolean>() {
        @Override public Boolean f( final MatchingGameState s ) {
            return s.info.timerVisible;
        }
    } );
    public final ObservableProperty<Double> barGraphAnimationTime = doubleProperty( new F<MatchingGameState, Double>() {
        @Override public Double f( final MatchingGameState s ) {
            return s.barGraphAnimationTime;
        }
    } );
    public final ObservableProperty<Mode> mode = new CompositeProperty<Mode>( new Function0<Mode>() {
        public Mode apply() {
            return state.get().getMode();
        }
    }, state );

    //Model property for whether the model should be revealing the clues (bar chart for fractions)
    public final ObservableProperty<Boolean> revealClues = new CompositeBooleanProperty( new Function0<Boolean>() {
        public Boolean apply() {
            return state.get().getMode() == Mode.SHOWING_WHY_ANSWER_WRONG ||
                   state.get().getMode() == Mode.USER_CHECKED_CORRECT_ANSWER ||
                   state.get().getMode() == Mode.SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS;
        }
    }, state );

    public final ObservableProperty<Long> timeInSec = new CompositeProperty<Long>( new Function0<Long>() {
        public Long apply() {
            return state.get().info.time / 1000L;
        }
    }, state );
    public final ObservableProperty<List<Integer>> fractionIDs = new CompositeProperty<List<Integer>>( new Function0<List<Integer>>() {
        public List<Integer> apply() {
            return state.get().fractions.map( new F<MovableFraction, Integer>() {
                @Override public Integer f( final MovableFraction m ) {
                    return m.id;
                }
            } );
        }
    }, state );


    //Property for the list of game results
    public final Property<List<GameResult>> gameResults = new Property<List<GameResult>>( state.get().gameResults ) {{
        state.addObserver( new VoidFunction1<MatchingGameState>() {
            public void apply( final MatchingGameState matchingGameState ) {
                set( state.get().gameResults );
            }
        } );
    }};

    //True if the user is choosing settings
    public final CompositeBooleanProperty choosingSettings = new CompositeBooleanProperty( new Function0<Boolean>() {
        public Boolean apply() {
            return state.get().info.mode == Mode.CHOOSING_SETTINGS;
        }
    }, state );

    private ObservableProperty<Double> doubleProperty( final F<MatchingGameState, Double> f ) {
        return new CompositeDoubleProperty( new Function0<Double>() {
            public Double apply() {
                return f.f( state.get() );
            }
        }, state );
    }

    private ObservableProperty<Integer> intProperty( final F<MatchingGameState, Integer> f ) {
        return new CompositeIntegerProperty( new Function0<Integer>() {
            public Integer apply() {
                return f.f( state.get() );
            }
        }, state );
    }

    private ObservableProperty<Boolean> booleanProperty( final F<MatchingGameState, Boolean> f ) {
        return new CompositeBooleanProperty( new Function0<Boolean>() {
            public Boolean apply() {
                return f.f( state.get() );
            }
        }, state );
    }
}