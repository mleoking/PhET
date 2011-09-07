// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.simsharing.SimsharingApplicationState;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.EndSession;
import edu.colorado.phet.simsharing.messages.GetActiveStudentList;
import edu.colorado.phet.simsharing.messages.GetStudentData;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.messages.SessionRecord;
import edu.colorado.phet.simsharing.messages.StartSession;
import edu.colorado.phet.simsharing.messages.StudentSummary;
import edu.colorado.phet.simsharing.socketutil.MessageHandler;
import edu.colorado.phet.simsharing.socketutil.MessageServer;
import edu.colorado.phet.simsharing.teacher.ClearDatabase;
import edu.colorado.phet.simsharing.teacher.ListAllSessions;
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
    private Map<SessionID, Session<?>> sessions = Collections.synchronizedMap( new HashMap<SessionID, Session<?>>() );

    private void start() throws IOException {
        new MessageServer( PORT, this ).start();
    }

    public void handle( Object message, ObjectOutputStream writeToClient, ObjectInputStream readFromClient ) throws IOException {
        if ( message instanceof GetStudentData ) {
            GetStudentData request = (GetStudentData) message;
            final Session<?> session = sessions.get( request.getSessionID() );
            final int requestedIndex = request.getIndex();
            final SimsharingApplicationState sample = session.getSample( requestedIndex );
            writeToClient.writeObject( new Pair<Object, Integer>( sample, requestedIndex == -1 ? session.getNumSamples() : requestedIndex ) );
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
        else if ( message instanceof GetActiveStudentList ) {
            final StudentList studentList = new StudentList( new ArrayList<StudentSummary>() {{
                for ( SessionID sessionID : new ArrayList<SessionID>( sessions.keySet() ) ) {
                    final Session<?> session = sessions.get( sessionID );
                    if ( session.isActive() ) {
                        add( session.getStudentSummary() );
                    }
                }
            }} );
            writeToClient.writeObject( studentList );
        }
        else if ( message instanceof AddSamples ) {
            AddSamples request = (AddSamples) message;
            sessions.get( request.getSessionID() ).addSamples( request );
        }
        else if ( message instanceof ListAllSessions ) {
            writeToClient.writeObject( new SessionList( new ArrayList<SessionRecord>() {{
                for ( Session<?> session : sessions.values() ) {
                    add( new SessionRecord( session.getSessionID(), session.getStartTime() ) );
                    Collections.sort( this, new Comparator<SessionRecord>() {
                        public int compare( SessionRecord o1, SessionRecord o2 ) {
                            return Double.compare( o1.getTime(), o2.getTime() );
                        }
                    } );
                }
            }} ) );
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