/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.common.clock2;

/**
 * User: Sam Reid
 * Date: Dec 15, 2005
 * Time: 12:24:51 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */

public class TestClock {
    private SwingClock swingClock;

    public TestClock() {
        swingClock = new SwingClock( 1000, new ConstantTimeConverter( 1.0 ) );
        swingClock.addClockListener( new ClockAdapter() {
            public void clockTicked( Clock clock ) {
                System.out.println( "TestClock.clockTicked" );
                System.out.println( "swingClock.getWallTime() = " + swingClock.getWallTime() );
                System.out.println( "swingClock.getWallTimeChangeMillis() = " + swingClock.getWallTimeChangeMillis() );
            }

            public void clockStarted( Clock clock ) {
                System.out.println( "TestClock.clockStarted" );
            }

            public void clockPaused( Clock clock ) {
                System.out.println( "TestClock.clockPaused" );
            }

            public void simulationTimeChanged( Clock clock ) {
                System.out.println( "TestClock.simulationTimeChanged" );
                System.out.println( "swingClock.getSimulationTimeChange() = " + swingClock.getSimulationTimeChange() );
            }

            public void clockReset( Clock clock ) {
                System.out.println( "TestClock.clockReset" );
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
    }
}
