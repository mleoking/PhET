// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.simsharingcore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

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

    //Fire an action when the mouse is released on the specified PNode
    public static void registerMouseReleasedListener( PNode node, final String action ) {
        node.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                actionPerformed( action );
            }
        } );
    }

    //Signify that an action has occurred by writing it to the appropriate sources, but only if the sim is running in "study mode" and is hence supposed to connect to the server
    public static void actionPerformed( String action ) {

        if ( connect ) {

            //Print the columns before the first message
            if ( !printedColumns ) {
                write( "Session ID" + "\t" + "Event time (ms)" + "\t" + "Action" );
                printedColumns = true;
            }

            //Send the message to the destination
            String timestamp = simStartedTime.isSome() ? ( System.currentTimeMillis() - simStartedTime.get() ) + "" : "@" + System.currentTimeMillis();
            write( SESSION_ID + "\t" + timestamp + "\t" + action );
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

    //Write the specified message when the button is pressed
    public static void addActionListener( ButtonNode textButtonNode, final String message ) {
        textButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SimSharingEvents.actionPerformed( message );
            }
        } );
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
    public static void simStarted( String[] args ) {
        simStartedTime = new Option.Some<Long>( System.currentTimeMillis() );
        connect = Arrays.asList( args ).contains( "-study" );
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

                    actionPerformed( "Sim started at time: " + simStartedTime );
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

    public static void addDragSequenceListener( PNode node, final Function0<String> message ) {
        node.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseDragged( PInputEvent event ) {
                actionPerformed( "Mouse dragged: " + message.apply() );
            }

            @Override public void mouseReleased( PInputEvent event ) {
                actionPerformed( "Mouse released: " + message.apply() );
            }
        } );
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