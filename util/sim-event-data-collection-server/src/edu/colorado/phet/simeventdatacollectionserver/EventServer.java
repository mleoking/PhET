// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionserver;

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

/**
 * Server that stores messages sent from SimSharingEvents.
 *
 * @author Sam Reid
 */
public class EventServer implements MessageHandler {

    private final Map<String, BufferedWriter> map = Collections.synchronizedMap( new HashMap<String, BufferedWriter>() );

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

            BufferedWriter bufferedWriter = getBufferedWriter( machineID, sessionID );
            bufferedWriter.write( stripMachineAndUserID( m, machineID, sessionID ) );
            bufferedWriter.newLine();
            bufferedWriter.flush();

            //TODO: is this the right message to end on?
            if ( object.equals( "system" ) && action.equals( "exited" ) ) {
                bufferedWriter.close();

                String key = getKey( machineID, sessionID );
                map.remove( key );

                System.out.println( "Session exited: " + m );
            }
        }
    }

    private String stripMachineAndUserID( String m, String machineID, String sessionID ) {
        String prefix = machineID + "\t" + sessionID + "\t";
        if ( m.startsWith( prefix ) ) {
            return m.substring( prefix.length() );
        }
        else {
            System.out.println( "Bogus prefix for message, returning full string: " + m );
            return m;
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
            bufferedWriter.write( "machineID = " + machineID + "\n" + "sessionID = " + sessionID + "\n" + "serverTime = " + System.currentTimeMillis() );
            bufferedWriter.newLine();
            bufferedWriter.flush();
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
            ObjectStreamMessageServer.HOST_IP_ADDRESS = args[list.indexOf( "-host" ) + 1];
        }
        System.out.println( "Using host: " + ObjectStreamMessageServer.HOST_IP_ADDRESS );
    }

    public static void main( String[] args ) throws IOException {
        EventServer.parseArgs( args );
        new EventServer().start();
    }
}