package edu.colorado.phet.fractionsintro.matchinggame.model;

import lombok.Data;

/**
 * Game state information, including level, mode, audio, checks.  Fairly game-independent.
 *
 * @author Sam Reid
 */
public @Data class GameInfo {

    //Level the user is on (1-based)
    public final int level;

    //Flag indicating whether audio is enabled
    public final boolean audio;

    //Number of times the user pressed "check answer" since the last collected response.
    public final int checks;

    //State for checks.
    public final Mode mode;

    //User's current score
    public final int score;

    //Features for time, time measured in seconds.
    public final long time;

    //Best time the user has attained on this level
    public final long bestTime;

    //Flag for whether the timer is visible
    public final boolean timerVisible;

    public GameInfo withAudio( final boolean audio ) { return new GameInfo( level, audio, checks, mode, score, time, bestTime, timerVisible ); }

    public GameInfo withChecks( final int checks ) { return new GameInfo( level, audio, checks, mode, score, time, bestTime, timerVisible );}

    public GameInfo withMode( final Mode mode ) { return new GameInfo( level, audio, checks, mode, score, time, bestTime, timerVisible ); }

    public GameInfo withScore( final int score ) { return new GameInfo( level, audio, checks, mode, score, time, bestTime, timerVisible ); }

    public GameInfo withTime( final long time ) { return new GameInfo( level, audio, checks, mode, score, time, bestTime, timerVisible ); }

    public GameInfo withBestTime( final long bestTime ) { return new GameInfo( level, audio, checks, mode, score, time, bestTime, timerVisible ); }

    public GameInfo withTimerVisible( final boolean timerVisible ) { return new GameInfo( level, audio, checks, mode, score, time, bestTime, timerVisible ); }
}