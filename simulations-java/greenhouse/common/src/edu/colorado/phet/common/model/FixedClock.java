package edu.colorado.phet.common.model;

import edu.colorado.phet.coreadditions.clock.ClockModel;

public class FixedClock extends DynamicClock {
    public FixedClock( ClockModel clockModel, ThreadPriority priority ) {
        super( clockModel, priority );
    }
    /**Overrides tickOnce to always run the same requestedDT.*/
    public void tickOnce( double dt ) {
        super.tickOnce( getRequestedDT() );
    }
}

