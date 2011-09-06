// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.EndSession;
import edu.colorado.phet.simsharing.messages.GetStudentData;
import edu.colorado.phet.simsharing.messages.GetStudentList;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.messages.StartSession;
import edu.colorado.phet.simsharing.messages.StudentSummary;
import edu.colorado.phet.simsharing.socketutil.MessageHandler;
import edu.colorado.phet.simsharing.socketutil.MessageServer;
import edu.colorado.phet.simsharing.teacher.ClearDatabase;
import edu.colorado.phet.simsharing.teacher.GetSessionList;
import edu.colorado.phet.simsharing.teacher.SessionList;
import edu.colorado.phet.simsharing.teacher.StudentList;

/**
 * @author Sam Reid
 */
public class Server implements MessageHandler {
    //Remote settings
//    public static int PORT = 44101;
//    public static String HOST_IP_ADDRESS = "128.138.145.107";//phet-server, but can be mutated to specify a different host

    //Settings for running locally
    public static String HOST_IP_ADDRESS = "localhost";
    public static int PORT = 1234;

    public static String[] names = new String[] { "Alice", "Bob", "Charlie", "Danielle", "Earl", "Frankie", "Gail", "Hank", "Isabelle", "Joe", "Kim", "Lucy", "Mikey", "Nathan", "Ophelia", "Parker", "Quinn", "Rusty", "Shirley", "Tina", "Uther Pendragon", "Vivian", "Walt", "Xander", "Yolanda", "Zed" };

    //Careful, used in many threads, so must threadlock
    private Map<SessionID, Session> sessions = Collections.synchronizedMap( new HashMap<SessionID, Session>() );

    private void start() throws IOException {
        new MessageServer( PORT, this ).start();
    }

    public void handle( Object message, ObjectOutputStream writeToClient, ObjectInputStream readFromClient ) throws IOException {
        if ( message instanceof GetStudentData ) {
            GetStudentData request = (GetStudentData) message;
            writeToClient.writeObject( sessions.get( request.getSessionID() ).getSample( request.getIndex() ) );
        }
        else if ( message instanceof StartSession ) {
            int sessionCount = sessions.size();
            final SessionID sessionID = new SessionID( sessionCount, names[sessionCount % names.length] );
            writeToClient.writeObject( sessionID );
            System.out.println( "session started: " + sessionID );

            sessions.put( sessionID, new Session( sessionID ) );
        }
        else if ( message instanceof EndSession ) {
            //Save the student info to disk and remove from system memory
            final SessionID sessionID = ( (EndSession) message ).getSessionID();
            System.out.println( "session exited: " + sessionID );
            sessions.get( sessionID ).endSession();
        }
        else if ( message instanceof GetStudentList ) {
            final StudentList studentList = new StudentList( new ArrayList<StudentSummary>() {{
                for ( SessionID sessionID : new ArrayList<SessionID>( sessions.keySet() ) ) {
                    add( sessions.get( sessionID ).getStudentSummary() );
                }
            }} );
            writeToClient.writeObject( studentList );
        }
        else if ( message instanceof AddSamples ) {
            AddSamples request = (AddSamples) message;
            sessions.get( request.getSessionID() ).addSamples( request );
        }
        else if ( message instanceof GetSessionList ) {
            final SessionList sessionList = new SessionList();
            //TODO: implement
//            final List<SessionStarted> sessionStarted = ds.find( SessionStarted.class ).asList();
//            Collections.sort( sessionStarted, new Comparator<SessionStarted>() {
//                public int compare( SessionStarted o1, SessionStarted o2 ) {
//                    return Double.compare( o1.getTime(), o2.getTime() );
//                }
//            } );
//            for ( SessionStarted started : sessionStarted ) {
//                sessionList.add( started );
//            }
            writeToClient.writeObject( sessionList );
        }
        else if ( message instanceof ClearDatabase ) {
            sessions.clear();
        }
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
        Server.parseArgs( args );
        new Server().start();
    }
}