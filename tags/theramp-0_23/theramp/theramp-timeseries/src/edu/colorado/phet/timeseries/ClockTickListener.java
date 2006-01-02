package edu.colorado.phet.timeseries;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;

/**
 * User: Sam Reid
 * Date: Dec 30, 2005
 * Time: 11:28:08 AM
 * Copyright (c) Dec 30, 2005 by Sam Reid
 */
public abstract class ClockTickListener implements ClockListener {
    public void clockStarted( ClockEvent clockEvent ) {
    }

    public void clockPaused( ClockEvent clockEvent ) {
    }

    public void simulationTimeChanged( ClockEvent clockEvent ) {
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {
    }
}
