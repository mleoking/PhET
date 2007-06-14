/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/tests-src/edu/colorado/phet/common/tests/TestModelClock.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.1 $
 * Date modified : $Date: 2006/01/31 18:47:24 $
 */
package edu.colorado.phet.common.tests;

import edu.colorado.phet.common.model.clock.ModelClock;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;

/**
 * TestModelClock
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1 $
 */
public class TestModelClock {

    public static void main( String[] args ) {
        ModelClock clock = new ModelClock( 250, 0.25 );
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                System.out.println( "tick: " + clockEvent.getSimulationTime() );
            }
        });
        clock.start();
        System.out.println( "clock.isPaused() = " + clock.isPaused() );

        // Wait 2 sec., then pause the clock
        try {
            Thread.sleep( 2000 );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        clock.pause();

        System.out.println( "clock.isPaused() = " + clock.isPaused() );

        // Wait 3 sec., then start the clock
        try {
            Thread.sleep( 3000 );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        clock.start();
        System.out.println( "clock.isPaused() = " + clock.isPaused() );

        // Wait 1 sec., then exit
        try {
            Thread.sleep( 1000 );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }

        System.out.println( "clock.isPaused() = " + clock.isPaused() );

    }
}
