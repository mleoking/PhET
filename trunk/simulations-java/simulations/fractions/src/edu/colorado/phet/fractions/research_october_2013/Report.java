// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_october_2013;

/**
 * Created by Sam on 10/18/13.
 */
public class Report {
    long startTime = System.currentTimeMillis();
    private long currentTime;

    public void update() {
        currentTime = System.currentTimeMillis();
    }

    @Override public String toString() {
        return "elapsed time: " + ( currentTime - startTime ) / 1000.0 + " sec";
    }
}
