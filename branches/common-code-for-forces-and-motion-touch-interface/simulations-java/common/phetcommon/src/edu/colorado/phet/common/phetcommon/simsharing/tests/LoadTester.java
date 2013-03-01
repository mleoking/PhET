// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.tests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;

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
    private static long count = 0;
    private final BufferedWriter bufferedWriter;

    private static final File dir = new File( System.getProperty( "user.home" ), "simsharing-load-testing/" + System.currentTimeMillis() );
    private int messageIndex = 0;

    static {
        dir.mkdirs();
        System.out.println( "DIR = " + dir.getAbsolutePath() );
    }

    public LoadTester( int eventsPerMinute,

                       //Don't all burst at the same time, but wait so that they are spaced out like real users
                       int initialDelayMillis ) throws IOException {
        this.eventsPerMinute = eventsPerMinute;
        this.initialDelayMillis = initialDelayMillis;

        count++;

        File dest = new File( dir, "log_" + count + ".txt" );

        bufferedWriter = new BufferedWriter( new FileWriter( dest, true ) );
    }

    private void start( String[] args ) {
        SimSharingManager.init( new PhetApplicationConfig( args, "test-java-project" ) );

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

        //Send a weird but identifiable message "menu iconified" for testing
        SimSharingManager.sendUserMessage( new UserComponent( "myMenu" ), UserComponentTypes.menu, UserActions.iconified, rand() );
        System.out.println( "LoadTester.sendEvent, eventCount = " + eventCount );

        try {
            bufferedWriter.write( "hello" );
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private ParameterSet rand() {
        int numParams = random.nextInt( 10 );
        ParameterSet p = new ParameterSet();
        for ( int i = 0; i < numParams; i++ ) {
            if ( i == 0 ) {
                p = p.with( ParameterKeys.messageIndex, messageIndex );
                messageIndex++;
            }
            else {
                p = p.with( randomParam( i ) );
            }
        }
        return p;
    }

    private Parameter randomParam( final int i ) {
        return new Parameter( new IParameterKey() {
            @Override public String toString() {
                return "key_" + i;
            }
        }, "value_" + i );
    }

    public static void main( final String[] args ) {
        System.out.println( System.getProperty( "java.class.path" ) );
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {

                int eventsPerMinute = parseInt( args[0] );
                try {
                    new LoadTester( eventsPerMinute, random.nextInt( 1000 ) ).start( args );
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
                new JFrame( "Control frame" ) {{
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                    setContentPane( new JLabel( "running..." ) );
                    setLocation( random.nextInt( 800 ), random.nextInt( 600 ) );
                }}.setVisible( true );
            }
        } );
    }
}