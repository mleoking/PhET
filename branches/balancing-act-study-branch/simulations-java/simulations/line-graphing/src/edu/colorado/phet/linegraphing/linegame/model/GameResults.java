// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Results for the "Line Graphing" game.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameResults {

    public final Property<Integer> score; // how many points the user has earned for the current game
    public boolean isNewBestTime; // is the time for the most-recently-completed game a new best time?
    private final HashMap<Integer, Long> bestTimes; // best times, maps level to time in ms

    public GameResults( IntegerRange levelsRange ) {

        score = new Property<Integer>( 0 );

        bestTimes = new HashMap<Integer, Long>();
        for ( int i = levelsRange.getMin(); i <= levelsRange.getMax(); i++ ) {
            bestTimes.put( i, 0L );
        }

        isNewBestTime = false;
    }

    // Gets the best time for a level. If there is no best time, returns zero.
    public long getBestTime( int level ) {
        Long bestTime = bestTimes.get( level );
        if ( bestTime == null ) {
            bestTime = 0L;
        }
        return bestTime;
    }

    // Updates the best time for the current level, at the end of a game with a perfect score.
    public void updateBestTime( int level, long time ) {
        isNewBestTime = false;
        if ( bestTimes.get( level ) == 0 ) {
            // there was no previous time for this level
            bestTimes.put( level, time );
        }
        else if ( time < bestTimes.get( level ) ) {
            // we have a new best time for this level
            bestTimes.put( level, time );
            isNewBestTime = true;
        }
    }
}
