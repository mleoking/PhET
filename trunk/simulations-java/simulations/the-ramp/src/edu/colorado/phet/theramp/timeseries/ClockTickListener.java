package edu.colorado.phet.theramp.timeseries;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;

/**
 * User: Sam Reid
 * Date: Dec 30, 2005
 * Time: 11:28:08 AM
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
