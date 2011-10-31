// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.eventserver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.simsharing.MessageHandler;
import edu.colorado.phet.common.phetcommon.simsharing.MessageServer;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;

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

    private final Map<String, BufferedWriter> map = Collections.synchronizedMap( new HashMap<String, BufferedWriter>() );

    private void start() throws IOException {
        new MessageServer( PORT, this ).start();
    }

    public void handle( Object message, ObjectOutputStream writeToClient, ObjectInputStream readFromClient ) throws IOException {
        if ( message != null ) {
            String m = message.toString();
            System.out.println( message );
            StringTokenizer st = new StringTokenizer( m, "\t" );
            String machineID = st.nextToken();
            String sessionID = st.nextToken();

            String object = st.nextToken();
            String action = st.nextToken();

            BufferedWriter bufferedWriter = getBufferedWriter( machineID, sessionID );
            bufferedWriter.write( m );
            bufferedWriter.newLine();
            bufferedWriter.flush();

            if ( object.equals( SimSharingEvents.OBJECT_SIM ) && action.equals( SimSharingEvents.ACTION_EXITED ) ) {
                bufferedWriter.close();

                String key = getKey( machineID, sessionID );
                map.remove( key );

                System.out.println( "Session exited: " + m );
            }
        }
    }

    private BufferedWriter getBufferedWriter( String machineID, String sessionID ) throws IOException {
        String key = getKey( machineID, sessionID );

        if ( map.containsKey( key ) ) {
            return map.get( key );
        }
        else {
            File file = new File( "/home/phet/eventserver/data/" + key + ".txt" );
            file.getParentFile().mkdirs();
            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( file, true ) );
            map.put( key, bufferedWriter );
            return bufferedWriter;
        }
    }

    private String getKey( String machineID, String sessionID ) {
        return machineID + "_" + sessionID;
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