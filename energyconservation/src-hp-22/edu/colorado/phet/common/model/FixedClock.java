package edu.colorado.phet.common.model;



public class FixedClock extends DynamicClock {
    public FixedClock( double dt, int waitTime, ThreadPriority priority ) {
        super( dt, waitTime, priority );
    }

    /**
     * Overrides tickOnce to always run the same requestedDT.
     */
    public void tickOnce( double dt ) {
        super.tickOnce( requestedDT );
    }
}

