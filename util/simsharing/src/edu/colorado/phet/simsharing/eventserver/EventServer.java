// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.eventserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.simsharing.MessageHandler;
import edu.colorado.phet.common.phetcommon.simsharing.MessageServer;

/**
 * Server that stores messages sent from SimSharingEvents.
 *
 * @author Sam Reid
 */
public class EventServer implements MessageHandler {
    public static String HOST_IP_ADDRESS = "128.138.145.107";//phet-server, but can be mutated to specify a different host
//    public static String HOST_IP_ADDRESS = "localhost";//Settings for running locally

    //On phet-server, port must be in a specific range of allowed ports, see Unfuddle ticket
    public static int PORT = 44101;

    private void start() throws IOException {
        new MessageServer( PORT, this ).start();
    }

    public void handle( Object message, ObjectOutputStream writeToClient, ObjectInputStream readFromClient ) throws IOException {
        System.out.println( message );
    }

    //Use phet-server for deployments, but localhost for local testing.
    public static void parseArgs( String[] args ) {
        final List<String> list = Arrays.asList( args );
        if ( list.contains( "-host" ) ) {
            HOST_IP_ADDRESS = args[list.indexOf( "-host" ) + 1];
        }
        System.out.println( "Using host: " + HOST_IP_ADDRESS );
    }

    public static void main( String[] args ) throws IOException {
        EventServer.parseArgs( args );
        new EventServer().start();
    }
}