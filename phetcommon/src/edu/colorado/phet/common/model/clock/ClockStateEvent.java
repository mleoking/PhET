/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.model.clock;

/**
 * @deprecated 
 */

public class ClockStateEvent extends ClockEvent{
    public ClockStateEvent( AbstractClock clock ) {
        super( clock );
    }

    public boolean getIsPaused() {
        return getClock().isPaused();
    }
}
