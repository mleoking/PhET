// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.tests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;

import static java.lang.Integer.parseInt;

/**
 * Tests the load of the simsharing feature on phet-server by sending lots of events.
 *
 * @author Sam Reid
 */
public class LoadTester {
    private final int eventsPerMinute;
    private final int initialDelayMillis;
    int eventCount = 0;
    private static final Random random = new Random();

    public LoadTester( int eventsPerMinute,

                       //Don't all burst at the same time, but wait so that they are spaced out like real users
                       int initialDelayMillis ) {
        this.eventsPerMinute = eventsPerMinute;
        this.initialDelayMillis = initialDelayMillis;
    }

    private void start( String[] args ) {
        SimSharingEvents.simStarted( new PhetApplicationConfig( args, "test-java-project" ) );

        System.out.println( "Started loadTester at " + eventsPerMinute + " events per minute" );
        double eventsPerSecond = eventsPerMinute / 60.0;
        double secondsPerEvent = 1.0 / eventsPerSecond;
        double millisPerEvent = secondsPerEvent * 1000;
        int delay = (int) millisPerEvent;
        System.out.println( "delay = " + delay );
        new Timer( delay, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                sendEvent();
            }
        } ) {{
            setInitialDelay( initialDelayMillis );
            setRepeats( true );
        }}.start();
    }

    private void sendEvent() {
        eventCount++;

        SimSharingEvents.sendEvent( "loadTester", "sentTest", rand() );
        System.out.println( "LoadTester.sendEvent, eventCount = " + eventCount );
    }

    private Parameter[] rand() {
        int numParams = random.nextInt( 10 );
        Parameter[] p = new Parameter[numParams];
        for ( int i = 0; i < p.length; i++ ) {
            p[i] = randomParam( i );
        }
        return p;
    }

    private Parameter randomParam( int i ) {
        return new Parameter( "key_" + i, "value_" + i );
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                int numClients = parseInt( args[0] );
                for ( int i = 0; i < numClients; i++ ) {

                    int eventsPerMinute = parseInt( args[1] );
                    new LoadTester( eventsPerMinute, random.nextInt( 1000 ) ).start( args );
                    new JFrame( "Control frame" ) {{
                        setDefaultCloseOperation( EXIT_ON_CLOSE );
                        setContentPane( new JLabel( "running..." ) );
                        setLocation( random.nextInt( 800 ), random.nextInt( 600 ) );
                    }}.setVisible( true );
                }
            }
        } );
    }
}