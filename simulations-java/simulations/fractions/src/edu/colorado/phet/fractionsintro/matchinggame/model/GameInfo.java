package edu.colorado.phet.fractionsintro.matchinggame.model;

import lombok.Data;

/**
 * @author Sam Reid
 */
public @Data class GameInfo {
    public final int level;

    public final boolean audio;

    //Number of times the user pressed "check answer" since the last collected response.
    public final int checks;

    //State for checks.
    public final Mode mode;
    public final double score;
    public final long time;
    public final long bestTime;
    public final boolean timerVisible;

    public GameInfo withMode( final Mode mode ) { return new GameInfo( level, audio, checks, mode, score, time, bestTime, timerVisible ); }

    public GameInfo withAudio( final boolean audio ) { return new GameInfo( level, audio, checks, mode, score, time, bestTime, timerVisible ); }

    public GameInfo withChecks( final int checks ) { return new GameInfo( level, audio, checks, mode, score, time, bestTime, timerVisible );}
}