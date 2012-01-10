// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Server that stores messages sent from SimSharingEvents.
 *
 * @author Sam Reid
 */
public class SimEventDataCollectionServer implements MessageHandler {

    FileStorage fileStorage = new FileStorage();

    private void start() throws IOException {
        new StringServer( ObjectStreamMessageServer.PORT, this ).start();
    }

    public void handle( Object message, ObjectOutputStream writeToClient, ObjectInputStream readFromClient ) throws IOException {
        if ( message != null ) {
            String m = message.toString();
            System.out.println( message );
            StringTokenizer st = new StringTokenizer( m, "\t" );
            String machineID = st.nextToken();
            String sessionID = st.nextToken();

            String time = st.nextToken();
            String object = st.nextToken();
            String action = st.nextToken();

            fileStorage.store( m, machineID, sessionID );

            //TODO: is this the right message to end on?
            if ( object.equals( "system" ) && action.equals( "exited" ) ) {
                fileStorage.close( machineID, sessionID );
                System.out.println( "Session exited: " + m );
            }
        }
    }


    //Use phet-server for deployments, but localhost for local testing.
    public static void parseArgs( String[] args ) {
        final List<String> list = Arrays.asList( args );
        if ( list.contains( "-host" ) ) {
            ObjectStreamMessageServer.HOST_IP_ADDRESS = args[list.indexOf( "-host" ) + 1];
        }
        System.out.println( "Using host: " + ObjectStreamMessageServer.HOST_IP_ADDRESS );
    }

    public static void main( String[] args ) throws IOException {
        SimEventDataCollectionServer.parseArgs( args );
        new SimEventDataCollectionServer().start();
    }
}