// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.linegraphing.linegame.model;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
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

    // Starts the timer. When the timer is started, it restarts from zero.
    public void start() {
        time.set( 0L );
        startTime = System.currentTimeMillis(); // don't use clock.getWallTime, it's not valid until the clock ticks
        clock.start();
    }

    // Stops the timer.
    public void stop() {
        clock.pause();
    }

    // Is the timer running?
    public boolean isRunning() {
        return clock.isRunning();
    }
}
