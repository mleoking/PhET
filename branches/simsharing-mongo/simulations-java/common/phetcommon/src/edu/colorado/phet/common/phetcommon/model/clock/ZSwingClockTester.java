// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.model.clock;

public class ZSwingClockTester extends ZAbstractClockTester {
    public ZSwingClockTester() {
        super( new ClockFactory() {
            public Clock createInstance( int defaultDelay, double v ) {
                SwingClock clock = new SwingClock( defaultDelay, v );

                clock.setCoalesce( false );

                return clock;
            }
        } );
    }
}
