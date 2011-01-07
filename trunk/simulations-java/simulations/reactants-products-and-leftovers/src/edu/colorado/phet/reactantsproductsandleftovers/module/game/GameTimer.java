// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * Game timer, keeps track of the elapsed time in the game.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class GameTimer {
    
    private final IClock clock;
    private final EventListenerList listeners;
    private long startTime; // System time in ms when the game started
    private long time; // ms
    
    public GameTimer( IClock clock ) {
        this.clock = clock;
        clock.addClockListener( new ClockAdapter() {
            @Override
            public void clockTicked( ClockEvent clockEvent ) {
                setTime( clockEvent.getWallTime() - startTime );
            }
        } );
        listeners = new EventListenerList();
    }

    /**
     * Starts the timer. 
     * When the timer is started, it restarts from zero.
     */
    public void start() {
        setTime( 0 );
        startTime = System.currentTimeMillis(); // don't use clock.getWallTime, it's not valid until the clock ticks
        clock.start();
    }
    
    /**
     * Stops the timer.
     */
    public void stop() {
        clock.pause();
    }
    
    /**
     * Gets the time, in ms.
     * @return
     */
    public long getTime() {
        return time;
    }
    
    /*
     * Sets the time, and notifies listeners.
     * @param time
     */
    private void setTime( long time ) {
        this.time = time;
        fireTimeChanged();
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( ChangeListener.class, listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( ChangeListener.class, listener );
    }
    
    /*
     * Notify listeners that the time has changed.
     */
    private void fireTimeChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners.getListeners( ChangeListener.class ) ) {
            listener.stateChanged( event );
        }
    }
}
