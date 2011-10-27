// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.dev;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.simsharingcore.DefaultActor;
import edu.colorado.phet.common.simsharingcore.ThreadedActor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class SimSharingEvents {

    //Generate a strong unique id, see http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string-in-java
    public static final String SESSION_ID = new BigInteger( 130, new SecureRandom() ).toString( 32 );

    //Flag to indicate whether the columns have been printed to the data source
    private static boolean printedColumns;

    public static void registerMouseReleasedListener( PNode node, final String action ) {
        node.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                actionPerformed( action );
            }
        } );
    }

    public static ThreadedActor client;

    static {
        try {
            client = new ThreadedActor( new DefaultActor() );
        }
        catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public static void actionPerformed( String action ) {
        if ( !printedColumns ) {
            write( "Session ID" + "\t" + "Event time (ms)" + "\t" + "Action" );
            printedColumns = true;
        }
        write( SESSION_ID + "\t" + System.currentTimeMillis() + "\t" + action );
    }

    private static void write( String s ) {
        System.out.println( s );
        try {
            client.tell( s );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public static void addActionListener( ButtonNode textButtonNode, final String s ) {
        textButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SimSharingEvents.actionPerformed( s );
            }
        } );
    }

    public static void addPropertyListener( final Property<Boolean> property, final String name ) {

        //Observe when the value changes whether by sim or by user, but don't notify about the initial value
        property.addObserver( new SimpleObserver() {
            public void update() {
                actionPerformed( name + ": " + property.get() );
            }
        }, false );
    }
}
