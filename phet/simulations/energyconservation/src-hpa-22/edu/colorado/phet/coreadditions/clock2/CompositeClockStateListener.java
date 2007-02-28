/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.clock2;

import java.util.ArrayList;

/**
 * Listens for changes in the execution state of the clock, ie whether it is running or paused.
 */
public class CompositeClockStateListener implements ClockStateListener {
    ArrayList list = new ArrayList();

    public void addClockStateListener( ClockStateListener csl ) {
        list.add( csl );
    }

    public void clockStarted( AbstractClock source ) {
        for( int i = 0; i < list.size(); i++ ) {
            ClockStateListener clockStateListener = (ClockStateListener)list.get( i );
            clockStateListener.clockStarted( source );
        }
    }

    public void clockStopped( AbstractClock source ) {
        for( int i = 0; i < list.size(); i++ ) {
            ClockStateListener clockStateListener = (ClockStateListener)list.get( i );
            clockStateListener.clockStopped( source );
        }
    }

    public void clockDestroyed( AbstractClock source ) {
        for( int i = 0; i < list.size(); i++ ) {
            ClockStateListener clockStateListener = (ClockStateListener)list.get( i );
            clockStateListener.clockDestroyed( source );
        }
    }

    public void clockDelayChanged( AbstractClock source, int newDelay ) {
        for( int i = 0; i < list.size(); i++ ) {
            ClockStateListener clockStateListener = (ClockStateListener)list.get( i );
            clockStateListener.clockDelayChanged( source, newDelay );
        }
    }
}
