// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lasers.view;

/**
 * Author: Sam Reid
 * Aug 23, 2007, 11:11:29 PM
 */
public class MatchState {
    private boolean match;
    private long time;
    private double matchingEnergy;
    private double e0;
    private double transitionEnergy;
    private double beamEnergy;

    public MatchState( boolean match, long time, double matchingEnergy, double e0, double transitionEnergy, double beamEnergy ) {
        this.match = match;
        this.time = time;
        this.matchingEnergy = matchingEnergy;
        this.e0 = e0;
        this.transitionEnergy = transitionEnergy;
        this.beamEnergy = beamEnergy;
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

    public double getE0() {
        return e0;
    }

    public double getBeamEnergy() {
        return beamEnergy;
    }

    public double getTransitionEnergy() {
        return transitionEnergy;
    }
}
