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
    private SwingClock swingClock;

    public TestClock() {
        swingClock = new SwingClock( 1000, new TimeConverter.Constant( 1.0 ), 1.0 );
        swingClock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clock ) {
                System.out.println( "TestClockEvent.clockTicked" );
                System.out.println( "swingClock.getWallTime() = " + swingClock.getWallTime() );
                System.out.println( "swingClockt.getWallTimeChangeMillis() = " + swingClock.getWallTimeChangeMillis() );
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

            public void clockReset( ClockEvent clock ) {
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
    }
}