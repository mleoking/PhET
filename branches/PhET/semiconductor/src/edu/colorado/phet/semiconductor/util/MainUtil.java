/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.util;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.*;
import edu.colorado.phet.semiconductor.common.FrameRate;
import edu.colorado.phet.semiconductor.common.FrameTimePrinter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jan 2, 2004
 * Time: 2:37:47 PM
 * Copyright (c) Jan 2, 2004 by Sam Reid
 */
public class MainUtil {
    public static void createControlFrame( final PhetApplication app, final int waitTime ) {
        JFrame controls = new JFrame( "Controls" );
        JButton setSwing = new JButton( "Swing CLock" );
        setSwing.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SwingTimerClock stc = new SwingTimerClock( 1, waitTime, true );
                switchClocks( stc, app );
            }
        } );
        JPanel controlPanel = new JPanel();

        controlPanel.add( setSwing );
        JButton setThread = new JButton( "Thread Clock" );
        setThread.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ThreadedClock tc = new ThreadedClock( 1, waitTime, true );

                switchClocks( tc, app );
            }
        } );

        controlPanel.add( setThread );

        final JSpinner waitTimeSpinner = new JSpinner( new SpinnerNumberModel( waitTime, 5, 10000, 5 ) );
        waitTimeSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Integer val = (Integer)waitTimeSpinner.getValue();

                app.getClock().setDelay( val.intValue() );
            }
        } );
        waitTimeSpinner.setBorder( BorderFactory.createTitledBorder( "Delay" ) );
        controlPanel.add( waitTimeSpinner );
        controls.setContentPane( controlPanel );
        controls.pack();
        controls.setVisible( true );

    }

    public static void addFrameRate( PhetApplication app ) {
        FrameRate rate = new FrameRate();
        rate.addObserver( new FrameTimePrinter( rate, 20 ) );
        app.getClock().addClockTickListener( rate );
    }


    /**
     * This is the payment we make for not using a pluggable TickSource strategy.
     */
    public static void switchClocks( AbstractClock newClock, PhetApplication app ) {
        /**Disables the original clock,
         * wires up the new clock to the listeners, and sets the new clock state to that of the old clock.
         */
        AbstractClock oldCloc = app.getClock();
        System.out.println( "oldCloc = " + oldCloc );
        oldCloc.stop();

        CompositeClockTickListener cctl = app.getClock().getTimeListeners();
        for( int i = 0; i < cctl.numClockTickListeners(); i++ ) {
            ClockTickListener listener = cctl.clockTickListenerAt( i );
            newClock.addClockTickListener( listener );
        }
        app.setClock( newClock );
        newClock.start();
    }

}
