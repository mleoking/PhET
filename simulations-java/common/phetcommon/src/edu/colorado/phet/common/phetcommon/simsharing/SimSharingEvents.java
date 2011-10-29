// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;

/**
 * Central access point for action-oriented message delivery for user usage studies.
 *
 * @author Sam Reid
 */
public class SimSharingEvents {

    //Generate a strong unique id, see http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string-in-java
    public static final String SESSION_ID = new BigInteger( 130, new SecureRandom() ).toString( 32 );

    //Flag to indicate whether the columns have been printed to the data source
    private static boolean printedColumns;

    //Actor for sending messages to the server
    public static ThreadedActor client;

    //Flag indicating whether messages should be sent to the server
    private static boolean connect = false;
    private static Option<Long> simStartedTime = new Option.None<Long>();
    private static Collection<String> queue = Collections.synchronizedCollection( new ArrayList<String>() );

    //Signify that an action has occurred by writing it to the appropriate sources, but only if the sim is running in "study mode" and is hence supposed to connect to the server
    public static void actionPerformed( String action, Parameter... parameters ) {

        if ( connect ) {

            //Print the columns before the first message
            if ( !printedColumns ) {
                write( "Session ID" + "\t" + "Event time (ms)" + "\t" + "Action" + "\t" + "Parameter List" );
                printedColumns = true;
            }

            //Send the message to the destination
            String timestamp = simStartedTime.isSome() ? ( System.currentTimeMillis() - simStartedTime.get() ) + "" : "@" + System.currentTimeMillis();

            String parameterText = new ObservableList<Parameter>( parameters ).mkString( ", " );
            write( SESSION_ID + "\t" + timestamp + "\t" + action + "\t" + parameterText );
        }
    }

    //Write the message to the console and to the server
    private static void write( String s ) {
        System.out.println( s );

        boolean shouldDeliver = connect;
        boolean canDeliver = client != null;

        if ( shouldDeliver && canDeliver ) {
            tellWithErrorHandling( s );
        }

        else if ( shouldDeliver && !canDeliver ) {
            queue.add( s );
        }
    }

    private static void tellWithErrorHandling( String s ) {
        try {
            client.tell( s );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

    //Write a message indicating that a property changed (whether the user or model changes the property)
    public static void addPropertyListener( final Property<Boolean> property, final String name ) {

        //Observe when the value changes whether by sim or by user, but don't notify about the initial value
        property.addObserver( new SimpleObserver() {
            public void update() {
                actionPerformed( name + ": " + property.get() );
            }
        }, false );
    }

    //Called from the first line of main(), connects to the server and sends a start message
    public static void simStarted( final PhetApplicationConfig config ) {
        simStartedTime = new Option.Some<Long>( System.currentTimeMillis() );
        connect = Arrays.asList( config.getCommandLineArgs() ).contains( "-study" );
        if ( connect ) {

            new Thread( new Runnable() {
                public void run() {
                    //Create the actor, but fail gracefully if cannot connect
                    try {
                        client = new ThreadedActor( new DefaultActor() );
                    }
                    catch ( ClassNotFoundException e ) {
                        e.printStackTrace();
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                    catch ( Throwable t ) {
                        t.printStackTrace();
                    }

                    actionPerformed( "Sim started",
                                     param( "time", simStartedTime.get() ),
                                     param( "name", config.getName() ),
                                     param( "version", config.getVersion().formatForAboutDialog() ),
                                     param( "project", config.getProjectName() ),
                                     param( "flavor", config.getFlavor() ),
                                     param( "locale", config.getLocale().toString() ),
                                     param( "distribution tag", config.getDistributionTag() ) );
                    actionPerformed( "Sim connected to server" );

                    //Report on any messages that were collected while we were trying to connect to the server
                    if ( client != null ) {
                        deliverQueue();
                    }
                    else {
                        System.out.println( "Weren't able to connect to the server even though we really wanted to." );
                    }
                }
            } ).start();
        }
    }

    private static synchronized void deliverQueue() {
        for ( String s : queue ) {
            tellWithErrorHandling( s );
        }
        queue.clear();
    }

    public static void addDragSequenceListener( JComponent component, final Function0<String> message ) {
        component.addMouseListener( new MouseAdapter() {
            @Override public void mousePressed( MouseEvent e ) {
                actionPerformed( "Mouse pressed: " + message.apply() );
            }
        } );
        component.addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged( MouseEvent e ) {
                actionPerformed( "Mouse dragged: " + message.apply() );
            }

            public void mouseMoved( MouseEvent e ) {
            }
        } );
    }
}