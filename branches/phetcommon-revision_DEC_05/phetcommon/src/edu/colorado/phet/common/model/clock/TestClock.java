/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.model.clock;

/**
 * User: Sam Reid
 * Date: Dec 15, 2005
 * Time: 12:24:51 PM
 * Copyright (c) Dec 15, 2005 by Sam Reid
 */

public class TestClock {
    private SwingTimerClock swingTimerClock;

    public TestClock() {
        swingTimerClock = new SwingTimerClock( 1000, new TimeConverter.Constant( 1.0 ), 1.0 );
        swingTimerClock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clock ) {
                System.out.println( "TestClockEvent.clockTicked" );
                System.out.println( "swingClock.getWallTime() = " + swingTimerClock.getWallTime() );
                System.out.println( "swingClockt.getWallTimeChangeMillis() = " + swingTimerClock.getWallTimeChangeMillis() );
            }

            public void clockStarted( ClockEvent clock ) {
                System.out.println( "TestClock.clockStarted" );
            }

            public void clockPaused( ClockEvent clock ) {
                System.out.println( "TestClock.clockPaused" );
            }

            public void simulationTimeChanged( ClockEvent clock ) {
                System.out.println( "TestClockt.simulationTimeChanged" );
                System.out.println( "swingClock.getSimulationTimeChange() = " + swingTimerClock.getSimulationTimeChange() );
            }

            public void clockReset( ClockEvent clock ) {
                System.out.println( "TestClockEvent.clockReset" );
            }

        } );
    }

    public static void main( String[] args ) throws InterruptedException {
        new TestClock().start();
    }

    private void start() throws InterruptedException {
        swingTimerClock.start();
        Thread.sleep( 5000 );
        swingTimerClock.pause();
        Thread.sleep( 5000 );
    }
}