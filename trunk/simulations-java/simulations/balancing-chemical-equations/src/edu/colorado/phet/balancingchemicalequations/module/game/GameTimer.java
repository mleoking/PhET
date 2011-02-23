// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Game timer, keeps track of the elapsed time in the game.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class GameTimer {

    private final IClock clock;
    private final Property<Long> timeProperty; // ms
    private long startTime; // System time in ms when the game started

    public GameTimer( IClock clock ) {
        this.clock = clock;
        this.timeProperty = new Property<Long>( 0L );
        clock.addClockListener( new ClockAdapter() {
            @Override
            public void clockTicked( ClockEvent clockEvent ) {
                timeProperty.setValue( clockEvent.getWallTime() - startTime );
            }
        } );
    }

    /**
     * Gets the time, in ms.
     * @return
     */
    public long getTime() {
        return timeProperty.getValue();
    }

    public void addTimeObserver( SimpleObserver o ) {
        timeProperty.addObserver( o );
    }

    public void removeTimeObserver( SimpleObserver o ) {
        timeProperty.removeObserver( o );
    }

    /**
     * Starts the timer.
     * When the timer is started, it restarts from zero.
     */
    public void start() {
        timeProperty.setValue( 0L );
        startTime = System.currentTimeMillis(); // don't use clock.getWallTime, it's not valid until the clock ticks
        clock.start();
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        clock.pause();
    }
}
