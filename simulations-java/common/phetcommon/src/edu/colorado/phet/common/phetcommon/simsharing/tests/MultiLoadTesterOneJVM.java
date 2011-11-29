// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.tests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.StringActor;
import edu.colorado.phet.common.phetcommon.simsharing.ThreadedActor;

/**
 * @author Sam Reid
 */
public class MultiLoadTesterOneJVM {
    private static Random random = new Random();

    public static void main( String[] args ) {
        for ( int i = 0; i < 100; i++ ) {
            final int finalI = i;
            Thread t = new Thread( new Runnable() {
                public void run() {

                    final String machineID = "machine_" + finalI;
                    final String sessionID = "session_" + finalI;
                    int eventsPerMinute = 120;
                    try {
                        final ThreadedActor client = new ThreadedActor( new StringActor() );
                        client.tell( "12354\t" + machineID + "\t" + sessionID + "\tobject\taction" );

                        double eventsPerSecond = eventsPerMinute / 60.0;
                        double secondsPerEvent = 1.0 / eventsPerSecond;
                        double millisPerEvent = secondsPerEvent * 1000;
                        int delay = (int) millisPerEvent;
                        System.out.println( "delay = " + delay );
                        final Property<Integer> count = new Property<Integer>( 0 );
                        new Timer( delay, new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                try {
                                    client.tell( "12354\t" + machineID + "\t" + sessionID + "\tobject\taction\tindex = " + count.get() );
                                    count.set( count.get() + 1 );
                                }
                                catch ( IOException e1 ) {
                                    e1.printStackTrace();
                                }
                            }
                        } ) {{
                            setInitialDelay( random.nextInt( 1000 ) );
                            setRepeats( true );
                        }}.start();
                    }
                    catch ( ClassNotFoundException e ) {
                        e.printStackTrace();
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                }
            } );
            t.start();

        }
    }
}
