// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.linegraphing.linegame.model;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Game timer, keeps track of the elapsed time in the game using "wall clock" time.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameTimer {

    // properties directly accessible by clients
    public final Property<Long> time; // ms

    private final IClock clock;
    private long startTime; // System time in ms when the game started

    /**
     * Constructor that uses a default clock.
     * The frame rate of this clock is sufficient for displaying a timer, but not for driving smooth animation.
     */
    public GameTimer() {
        this( new ConstantDtClock( 1000 / 5, 1 ) ); // don't need to tick too often to update the scoreboard time
    }

    public GameTimer( IClock clock ) {
        this.clock = clock;
        this.time = new Property<Long>( 0L );
        clock.addClockListener( new ClockAdapter() {
            @Override
            public void clockTicked( ClockEvent clockEvent ) {
                time.set( clockEvent.getWallTime() - startTime );
            }
        } );
    }

    /**
     *  Is the timer running?
     *  @returns true or false
     */
    public boolean isRunning() {
        return clock.isRunning();
    }

    /**
     * Starts the timer. When the timer is started, it restarts from zero.
     * If the timer is already running, this is a no-op.
     */
    public void start() {
        if ( !isRunning() ) {
            time.set( 0L );
            startTime = System.currentTimeMillis(); // don't use clock.getWallTime, it's not valid until the clock ticks
            clock.start();
        }
    }

    /**
     * Stops the timer.
     * If the timer is already stopped, this is a no-op.
     */
    public void stop() {
        if ( isRunning() ) {
            clock.pause();
        }
    }

    /**
     *  Convenience method for starting/stopping the timer.
     */
    public void setRunning( boolean running ) {
        if ( running ) {
            start();
        }
        else {
            stop();
        }
    }
}
