package edu.colorado.phet.semiconductor.common;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;

/**
 * User: Sam Reid
 * Date: Feb 6, 2004
 * Time: 1:01:39 PM
 * Copyright (c) Feb 6, 2004 by Sam Reid
 */
public class OneShotAction implements ClockTickListener {
    Runnable action;
    private int tickCount;
    int numTicks = 0;

    public OneShotAction( Runnable action, int tickCount ) {
        this.action = action;
        this.tickCount = tickCount;
    }

    public void clockTicked( AbstractClock abstractClock, double v ) {
        numTicks++;
        if( numTicks > tickCount ) {
            action.run();
            abstractClock.removeClockTickListener( this );
        }
    }
}
