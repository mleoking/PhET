/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.model.clock;

/**
 * @deprecated
 */
public class ClockTickEvent extends ClockEvent {

    public ClockTickEvent( AbstractClock clock ) {
        super( clock );
    }

    /**
     * @deprecated
     */
    public double getDt() {
        return super.getSimulationTimeChange();
    }
}
