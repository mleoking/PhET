// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.util.HashMap;

import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;

/**
 * Model for the "Line Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGameModel {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( LineGameModel.class.getCanonicalName() );

    // phases of a game, mutually exclusive
    public enum GamePhase {
        SETTINGS,  // user is choosing game settings
        PLAY, // user is playing the game
        RESULTS, // user is receiving results of playing the game
        REWARD // user is receiving a reward for exceptional game play
    }

    /*
     * States during the "play" phase of a game, mutually exclusive.
     * For lack of better names, the state names correspond to the main action that
     * the user can take in that state.  For example. the CHECK state is where the user
     * can enter coefficients and press the "Check" button to check their answer.
     */
    public static enum PlayState {
        CHECK,
        TRY_AGAIN,
        SHOW_ANSWER,
        NEXT,
        NEW_GAME
    }

    public final GameSettings settings;
    public final GameTimer timer;
    private final HashMap<Integer, Long> bestTimes; // best times, maps level to time in ms
    private boolean isNewBestTime; // is the time for this game a new best time?
    public final Property<Integer> score; // how many points the user has earned for the current game
    public final Property<GamePhase> phase;
    public final Property<PlayState> state;

    public LineGameModel() {

        settings = new GameSettings( new IntegerRange( 1, 3 ), false, false );

        score = new Property<Integer>( 0 );

        // time
        timer = new GameTimer( new ConstantDtClock( 1000 / 5, 1 ) );
        bestTimes = new HashMap<Integer, Long>();
        for ( int i = settings.level.getMin(); i <= settings.level.getMax(); i++ ) {
            bestTimes.put( i, 0L );
        }

        phase = new Property<GamePhase>( GamePhase.SETTINGS ) {{
            //XXX Problem: best time won't necessarily be updated before other observers are notified.
            addObserver( new VoidFunction1<GamePhase>() {
                public void apply( GamePhase phase ) {
                    LOGGER.info( "game phase = " + phase );
                    if ( phase == GamePhase.PLAY ) {
                        timer.start();
                    }
                    else {
                        timer.stop();
                        // record new best time
                        if ( settings.timerEnabled.get() && ( timer.time.get() < bestTimes.get( settings.level.get() ) ) ) {
                            bestTimes.put( settings.level.get(), timer.time.get() );
                        }
                    }
                }
            } );
        }};

        state = new Property<PlayState>( PlayState.CHECK ) {{
            addObserver( new VoidFunction1<PlayState>() {
                public void apply( PlayState state ) {
                    LOGGER.info( "play state = " + state );
                    //XXX
                }
            } );
        }};
    }

    public int getPerfectScore() {
       return 10; //XXX compute based on point scheme and number of challenges per game
    }

    /**
     * Gets the best time for a specified level.
     * If this returns zero, then there is no best time for the level.
     *
     * @param level
     * @return
     */
    public long getBestTime( int level ) {
        Long bestTime = bestTimes.get( level );
        if ( bestTime == null ) {
            bestTime = 0L;
        }
        return bestTime;
    }

    public boolean isNewBestTime() {
        return isNewBestTime;
    }
}