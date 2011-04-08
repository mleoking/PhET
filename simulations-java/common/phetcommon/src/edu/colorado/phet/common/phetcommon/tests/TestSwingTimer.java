// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.tests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Demonstrates the problem with using a Swing Timer when events are coalesced.
 * When the amount of work done in the action listener is nearly the same value as the wait time
 * on the clock (or more), the delay before next tick is unnecessarily high.
 * <p/>
 * The solution to this problem is to setCoalesce(false) on the clock.
 * Since this may have unintended impact on existing simulations in terms of UI responsiveness,
 * or other aspects of the simulation, this is to be turned-on on a simulation-by-simulation basis.
 */
public class TestSwingTimer {
    static long lastEndTime;

    public static void main( String[] args ) throws InterruptedException {
        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                long betweenTime = System.currentTimeMillis() - lastEndTime;
                System.out.print( "betweenTime = " + betweenTime + ", " );
                try {
                    Thread.sleep( 30 );
                }
                catch ( InterruptedException e1 ) {
                    e1.printStackTrace();
                }
                long tickTime = System.currentTimeMillis() - lastEndTime;
                System.out.println( "tickTime = " + tickTime );
                lastEndTime = System.currentTimeMillis();
            }
        } );
        timer.setCoalesce( false );
        timer.start();
        Thread.sleep( 5000 );
        System.exit( 0 );
    }
}
