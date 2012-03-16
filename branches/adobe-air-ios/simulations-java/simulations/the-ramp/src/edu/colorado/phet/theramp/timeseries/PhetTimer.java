// Copyright 2002-2011, University of Colorado

/*PhET, 2004.*/
package edu.colorado.phet.theramp.timeseries;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:45:47 AM
 */
public class PhetTimer {
    private String name;
    private double time = 0;
    private ArrayList listeners = new ArrayList();

    public PhetTimer( String name ) {
        this.name = name;
    }

    public static interface Listener {
        void timeChanged();
    }

    public void stepInTime( double dt, double maxTime ) {
        double origTime = time;
        time += dt;
        time = Math.min( time, maxTime );
        if ( time != origTime ) {
            updateObservers();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void updateObservers() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.timeChanged();
        }
    }

    public String toString() {
        return "PhetTimer, name=" + name;
    }

    public double getTime() {
        return time;
    }

    public void reset() {
        this.time = 0;
        updateObservers();
    }

    public void setTime( double time ) {
        this.time = time;
        updateObservers();
    }

}
