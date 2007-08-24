package edu.colorado.phet.lasers.view;

/**
 * Author: Sam Reid
* Aug 23, 2007, 11:11:29 PM
*/
public class MatchState {
    boolean match;
    long time;
    private double matchingEnergy;

    public MatchState( boolean match, long time, double matchingEnergy ) {
        this.match = match;
        this.time = time;
        this.matchingEnergy = matchingEnergy;
    }

    public boolean isMatch() {
        return match;
    }

    public long getTime() {
        return time;
    }

    public double getMatchingEnergy() {
        return matchingEnergy;
    }
}
