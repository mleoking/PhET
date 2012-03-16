// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14677 $
 * Date modified : $Date:2007-04-17 03:40:29 -0500 (Tue, 17 Apr 2007) $
 */
package edu.colorado.phet.common.phetcommon.tests;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;

/**
 * User: Sam Reid
 * Date: Dec 15, 2005
 * Time: 12:24:51 PM
 */

public class TestClock {
    private SwingClock swingClock;

    public TestClock() {
        swingClock = new SwingClock( 1000 /* delay */, 1.0 /* constant dt */ );
        swingClock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clock ) {
                System.out.println( "TestClockEvent.clockTicked" );
                System.out.println( "swingClock.getWallTime() = " + swingClock.getWallTime() );
                System.out.println( "swingClockt.getWallTimeChangeMillis() = " + swingClock.getWallTimeChange() );
            }

            public void clockStarted( ClockEvent clock ) {
                System.out.println( "TestClock.clockStarted" );
            }

            public void clockPaused( ClockEvent clock ) {
                System.out.println( "TestClock.clockPaused" );
            }

            public void simulationTimeChanged( ClockEvent clock ) {
                System.out.println( "TestClockt.simulationTimeChanged" );
                System.out.println( "swingClock.getSimulationTimeChange() = " + swingClock.getSimulationTimeChange() );
            }

            public void simulationTimeReset( ClockEvent clock ) {
                System.out.println( "TestClockEvent.clockReset" );
            }

        } );
    }

    public static void main( String[] args ) throws InterruptedException {
        new TestClock().start();
    }

    private void start() throws InterruptedException {
        swingClock.start();
        Thread.sleep( 5000 );
        swingClock.pause();
        Thread.sleep( 5000 );
        System.exit( 0 );
    }
}